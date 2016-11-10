package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import common.vo.Human;
import common.vo.Lecture;
import common.vo.Professor;
import common.vo.Student;

public class ServerDB {

	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:XE";
	private String user = "hr";
	private String password = "hr";

	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	public void makeConnection() {
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("ServerDB> Driver로딩 완료");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} // t-c
	}// makeConnection()

	public void closeAll() {
		try {
			if (conn != null)
				conn.close();
			if (pstmt != null)
				pstmt.close();
			if (rs != null)
				rs.close();
			System.out.println("ServerDB> Closed All");
		} catch (SQLException e) {
			e.printStackTrace();
		} // t-c
	}// closeAll()

	/**
	 * 운영자가 학생또는 교수 신규생성 할 때 사용 성공하면 true, 실패(regno중복)하면 false반환
	 * 
	 * @param Human객체
	 */
	public boolean insertHuman(Human h) {
		makeConnection();
		try {
			if (h instanceof Professor) {
				Professor p = (Professor) h;
				String sql = "INSERT INTO professor (p_regno, p_password, p_name, p_major) values (?, default, ?, ?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, "P" + p.getRegNo());
				pstmt.setString(2, p.getName()); 
				pstmt.setString(3, p.getMajor());
				pstmt.executeUpdate();
				System.out.println("ServerDB> 신규교수 DB저장완료");
			} else if (h instanceof Student) {
				Student s = (Student) h;
				String sql = "INSERT INTO student (s_regno, s_password, s_name, s_major, s_grade, s_nowcredit) values (?, default, ?, ?, ?, default)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, "S" + s.getRegNo());
				pstmt.setString(2, s.getName());
				pstmt.setString(3, s.getMajor());
				pstmt.setString(4, s.getGrade());
				pstmt.executeUpdate();
				System.out.println("ServerDB> 신규학생 DB저장완료");
			}
		} catch (SQLException e) { // regNo가 primaryKey이기 때문에 중복된 사람 입력하면
									// SQLException발생
			return false;
		} finally {
			closeAll();
		}
		return true;
	}// insertHuman()

	/**
	 * 운영자가 regNo기준으로 사람을 찾는다.
	 * 
	 * @param regNo
	 * @return 찾았으면 human객체, 못찾았으면(=regNo가 없으면) null 반환
	 */
	public Human searchHuman(String regNo, String gubun) {
		Human result = null;
		makeConnection();
		try {
			if (gubun.equals("교수")) {
				String sql = "SELECT p_name, p_major FROM professor WHERE p_regno = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, "P" + regNo);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					result = new Professor(rs.getString(1), regNo, rs.getString(2));
					System.out.println("ServerDB> 교수 검색 완료");
				}
			} else if (gubun.equals("학생")) {
				String sql = "SELECT s_name, s_major, s_grade, s_nowcredit FROM student WHERE s_regno = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, "S" + regNo);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					result = new Student(rs.getString(1), regNo, rs.getString(2), rs.getString(3), rs.getInt(4));
					System.out.println("ServerDB> 학생 검색 완료");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		return result;
	}// searchHuman()

	/**
	 * 운영자가 regNo기준으로 사람 삭제할때 씀 삭제성공하면 true, 실패하면(=regno없는사람이면) false반환
	 * 사람 삭제와 동시에 교수면, 개설된강의 모두 삭제되고 해당과목 수강생 정보도 연쇄수정.
	 * 학생이면 해당 학생이 수강중인 강의정보 수정
	 * @param regNo
	 * @return
	 */
	public boolean deleteHuman(String regNo, String grade) {		
//		교수삭제 - 교수의 모든 lecture코드 array에 받아 놓고, 해당교수 open삭제
//		array의 lecture코드 기준으로 해당과목 수강생 학점 -3 , register삭제, lecture삭제, professor 삭제
		boolean result = false;
		makeConnection();
		try {
			if (grade.equals("교수")) {
				ArrayList<String> lectureCodeList = new ArrayList<>();
				String getLecture = "SELECT l_code FROM open WHERE p_regno = ?";
				pstmt = conn.prepareStatement(getLecture);
				pstmt.setString(1, "P" + regNo);
				rs = pstmt.executeQuery();
				while(rs.next()){
					lectureCodeList.add(rs.getString(1));
				}
				
				String deleteOpen = "DELETE open WHERE p_regno = ?";
				pstmt = conn.prepareStatement(deleteOpen);
				pstmt.setString(1, "P" + regNo);
				pstmt.executeUpdate();
				
				String minusCredit = "UPDATE student SET s_nowcredit = (s_nowcredit - 3) "
						+ "WHERE s_regno in (SELECT s_regno FROM register WHERE l_code = ?)";
				for (String lectureCode : lectureCodeList) {
					pstmt = conn.prepareStatement(minusCredit);
					pstmt.setString(1, lectureCode);
					pstmt.executeUpdate();
				}
				
				String deleteRegister = "DELETE register WHERE l_code = ?";
				for (String lectureCode : lectureCodeList) {
					pstmt = conn.prepareStatement(deleteRegister);
					pstmt.setString(1, lectureCode);
					pstmt.executeUpdate();
				}
				
				String deleteLecture = "DELETE lecture WHERE l_code = ?";
				for (String lectureCode : lectureCodeList) {
					pstmt = conn.prepareStatement(deleteLecture);
					pstmt.setString(1, lectureCode);
					pstmt.executeUpdate();
				}
				
				String deleteProf = "DELETE professor WHERE p_regno = ?";
				pstmt = conn.prepareStatement(deleteProf);
				pstmt.setString(1, "P" + regNo);
				int a = pstmt.executeUpdate();
				if(a > 0) result = true;
				
			} else {
//				학생 삭제 - lecture에서 nowstudent -1, register 삭제, student 삭제
				
				String updateLecture = "UPDATE lecture SET l_nowstudent = (l_nowstudent - 1) "
						+ "WHERE l_code in (SELECT l_code FROM register WHERE s_regno = ?)";
				pstmt = conn.prepareStatement(updateLecture);
				pstmt.setString(1, "S" + regNo);
				pstmt.executeUpdate();
				
				String deleteReg = "DELETE register WHERE s_regno = ?";
				pstmt = conn.prepareStatement(deleteReg);
				pstmt.setString(1, "S" + regNo);
				pstmt.executeUpdate();
				
				String deleteStd = "DELETE student WHERE s_regno = ?";
				pstmt = conn.prepareStatement(deleteStd);
				pstmt.setString(1, "S" + regNo);
				int a = pstmt.executeUpdate();
				if(a > 0) result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		return result;
	}// deleteHuman()

	/**
	 * 운영자가 Human객체 받아서 정보수정 할때 씀 (덮어씌움) 성공하면 true, 실패하면(regNo없는사람이면) false 반환
	 * 
	 * @param h
	 * @return
	 */
	public boolean updateHuman(Human h) {
		boolean result = false;
		makeConnection();
		try {
			if (h instanceof Professor) {
				Professor p = (Professor) h;
				String sql = "UPDATE professor SET p_name = ?, p_major = ? WHERE p_regno = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, p.getName());
				pstmt.setString(2, p.getMajor());
				pstmt.setString(3, "P" + p.getRegNo());
				int i = pstmt.executeUpdate();
				if (i != 0)
					result = true;
			} else if (h instanceof Student) {
				Student s = (Student) h;
				String sql = "UPDATE student SET s_name = ?, s_major = ?, s_grade = ? WHERE s_regno = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, s.getName());
				pstmt.setString(2, s.getMajor());
				pstmt.setString(3, s.getGrade());
				pstmt.setString(4, "S" + s.getRegNo());
				int i = pstmt.executeUpdate();
				if (i != 0)
					result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		return result;
	}// updateHuman()

	/**
	 * 교수 먼저 담고 학생 담아서 ArrayList로 반환
	 * 
	 * @return
	 */
	public ArrayList<Human> searchAllHuman() {
		ArrayList<Human> result = new ArrayList<>();
		makeConnection();
		try {
			String sql = "SELECT p_regno, p_name, p_major FROM professor ORDER BY p_major, p_name";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(new Professor(rs.getString(1).substring(1), rs.getString(2), rs.getString(3)));
			}

			String sql2 = "SELECT s_name, s_regno, s_major, s_grade, s_nowcredit FROM student ORDER BY s_major, s_grade, s_name";
			pstmt = conn.prepareStatement(sql2);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(new Student(rs.getString(1), rs.getString(2).substring(1), rs.getString(3), rs.getString(4),
						rs.getInt(5)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		return result;
	}// showAll()

	/**
	 * 로그인 요청시 계정과 암호가 맞는지 확인 있으면 true, 없으면 false반환
	 * 
	 * @param h
	 * @return
	 */
	public Human logInCheck(Human h) {
		Human result = null;
		makeConnection();
		if (h instanceof Professor) {
			Professor p = (Professor) h;
			String sql = "SELECT p_name, p_major FROM professor WHERE p_regno = ? AND p_password = ?";
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, "P" + p.getRegNo());
				pstmt.setString(2, p.getPw());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					p.setName(rs.getString(1));
					p.setMajor(rs.getString(2));
					p.setRegNo("P" + p.getRegNo());
					result = p;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				closeAll();
			}
		} else {
			Student s = (Student) h;
			String sql = "SELECT s_name, s_major, s_grade, s_nowcredit FROM student WHERE s_regno = ? AND s_password = ?";
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, "S" + s.getRegNo());
				pstmt.setString(2, s.getPw());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					s.setName(rs.getString(1));
					s.setMajor(rs.getString(2));
					s.setGrade(rs.getString(3));
					s.setNowCredit(rs.getInt(4));
					s.setRegNo("S" + s.getRegNo());
					result = s;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				closeAll();
			}
		}
		return result;
	}

	/**
	 * 생성에 성공하면 1 실패(=시간,강의실 동시에 겹침)하면 -1 (과목코드겹치면 -2) 내가 이미 갖고있는 수업시간이면 -3
	 * SQLException은 0 반환
	 * 
	 * @param p
	 * @param l
	 * @return
	 */
	public int createLecture(Professor p, Lecture l) {
		makeConnection();
		try {

			// 같은 시간에 나한테 수업이 있는지 먼저 확인
			String chkMyTime = "SELECT l.l_code FROM lecture l, open o WHERE l_time = ? AND o.p_regno = ? AND l.l_code = o.l_code";
			pstmt = conn.prepareStatement(chkMyTime);
			pstmt.setString(1, l.getTime());
			pstmt.setString(2, p.getRegNo());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return -3;
			}

			String chkDuplicateSQL = "SELECT l_code FROM lecture WHERE l_time = ? AND l_room = ?"; // 시간.강의실
																									// /
																									// 코드
																									// 중복검사
			pstmt = conn.prepareStatement(chkDuplicateSQL);
			pstmt.setString(1, l.getTime());
			pstmt.setString(2, l.getRoom());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getString(1).equals(l.getSubjectCode()))
					return -2;
				return -1;
			} else {
				String insertSQL = "INSERT INTO lecture VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // 강의테이블에
																										// 정보입력
				pstmt = conn.prepareStatement(insertSQL);
				pstmt.setString(1, l.getSubjectCode());
				pstmt.setString(2, l.getSubject());
				pstmt.setString(3, l.getMajor());
				pstmt.setString(4, l.getType());
				pstmt.setString(5, l.getGrade());
				pstmt.setInt(6, l.getMaxStudentNum());
				pstmt.setInt(7, 0);
				pstmt.setString(8, l.getTime());
				pstmt.setString(9, l.getRoom());
				switch (l.getTime().charAt(0)) {
				case '월':
					pstmt.setString(10, "1");
					break;
				case '화':
					pstmt.setString(10, "2");
					break;
				case '수':
					pstmt.setString(10, "3");
					break;
				case '목':
					pstmt.setString(10, "4");
					break;
				case '금':
					pstmt.setString(10, "5");
					break;
				}
				switch (l.getTime().charAt(2)) {
				case '1':
					pstmt.setString(11, "1");
					break;
				case '4':
					pstmt.setString(11, "4");
					break;
				case '7':
					pstmt.setString(11, "7");
					break;
				}
				pstmt.setString(12, "3");
				pstmt.setString(13, l.getPlan());
				pstmt.executeUpdate();
				System.out.println("ServerDB> 강의정보 입력완료");

				String relationSQL = "INSERT INTO open VALUES(?, ?)"; // 교수랑 강의
																		// 관계설정(open테이블)
				pstmt = conn.prepareStatement(relationSQL);
				pstmt.setString(1, p.getRegNo());
				pstmt.setString(2, l.getSubjectCode());
				pstmt.executeUpdate();
				System.out.println("ServerDB> 교수,강의 관계설정 완료");
				return 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		} finally {
			closeAll();
		}
	}

	/**
	 * 해당 교수의 전체 강의목록을 arrayList로 반환
	 * 
	 * @param p
	 * @return
	 */
	public ArrayList<Lecture> getAllLecture(Professor p) {
		ArrayList<Lecture> result = new ArrayList<>();
		makeConnection();
		try {
			String sql = "SELECT l.l_code, l_subject, l_major, l_type, l_grade, l_maxstudent, l_nowstudent, l_time, l_room, l_time_index, l_module, l_plan "
					+ "FROM lecture l, open o WHERE o.p_regno = ? AND l.l_code = o.l_code ORDER BY substr(l_time, 2), l_time_index";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, p.getRegNo());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Lecture l = new Lecture();
				l.setSubjectCode(rs.getString(1));
				l.setSubject(rs.getString(2));
				l.setMajor(rs.getString(3));
				l.setType(rs.getString(4));
				l.setGrade(rs.getString(5));
				l.setMaxStudentNum(rs.getInt(6));
				l.setNowStudentNum(rs.getInt(7));
				l.setTime(rs.getString(8));
				l.setRoom(rs.getString(9));
				l.setTime_index(rs.getString(10));
				l.setModule(rs.getString(11));
				l.setPlan(rs.getString(12));
				result.add(l);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		System.out.println("ServerDB> 해당교수 전체 강의목록 검색후 리턴완료");
		return result;
	}

	/**
	 * 해당 강의를 삭제. 먼저 수강중인 학생과의 관계를 끊고 담당 교수와의 관계를 끊고 강의 데이터 row 자체를 날림
	 * 
	 * @param l
	 * @return
	 */
	public boolean deleteLecture(Lecture l) {
		boolean result = false;

		makeConnection();
		try {
			String sql = "DELETE open WHERE l_code = ?"; // 개강정보 날리고
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, l.getSubjectCode());
			pstmt.executeUpdate();

			String sql1 = "DELETE register WHERE l_code = ?"; // 수강정보 날리고
			pstmt = conn.prepareStatement(sql1);
			pstmt.setString(1, l.getSubjectCode());
			pstmt.executeUpdate();

			String sql2 = "DELETE lecture WHERE l_code = ?"; // 강의객체 날리고
			pstmt = conn.prepareStatement(sql2);
			pstmt.setString(1, l.getSubjectCode());
			int i = pstmt.executeUpdate();
			if (i == 1)
				result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		return result;
	}

	/**
	 * 강의정보 수정. 매개변수로 받은 강의를 기준으로 insert와 비슷한 처리. 강의객체 자체에 담당교수 객체가 담겨서 옴
	 * 
	 * 수정에 성공하면 1 실패(=시간,강의실 동시에 겹침)하면 -1 내가 이미 갖고있는 수업시간이면 -3
	 * SQLException은 0 반환
	 * 
	 * @return
	 */
	public int updateLecture(Lecture l) {
		makeConnection();
		try {
			conn.setAutoCommit(false);
			// 같은 시간에 나한테 수업이 있는지 먼저 확인
			System.out.println(l.getTime());
			System.out.println(l.getOldCode());
			System.out.println(l.getProfessor().getRegNo());
			String chkMyTime = "SELECT l.l_code FROM lecture l, open o WHERE l.l_code = o.l_code AND l_time = ? AND l.l_code != ? AND o.p_regno = ?";
			pstmt = conn.prepareStatement(chkMyTime);
			pstmt.setString(1, l.getTime());
			pstmt.setString(2, l.getOldCode());
			pstmt.setString(3, l.getProfessor().getRegNo());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return -3;
			}

			String chkDuplicateSQL = "SELECT l_code FROM lecture WHERE l_time = ? AND l_room = ? AND l_code != ?"; 	// 시간.강의실
																									// 코드
																									// 중복검사
			pstmt = conn.prepareStatement(chkDuplicateSQL);
			pstmt.setString(1, l.getTime());
			pstmt.setString(2, l.getRoom());
			pstmt.setString(3, l.getOldCode());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return -1;
			} else {
				 
				String deleteOpen = "DELETE open WHERE l_code = ?"; // Open관계 삭제
				PreparedStatement pstmtForDeleteOpen = conn.prepareStatement(deleteOpen);
				pstmtForDeleteOpen.setString(1, l.getOldCode());
				pstmtForDeleteOpen.executeUpdate();

				String deleteLecture = "DELETE lecture WHERE l_code = ?"; // 기존
																			// 강의
																			// 삭제
				PreparedStatement pstmtForDeleteLecture = conn.prepareStatement(deleteLecture);
				pstmtForDeleteLecture.setString(1, l.getOldCode());
				pstmtForDeleteLecture.executeUpdate();

				String updateLecture = "INSERT INTO lecture VALUES (?,?,?,?,?,?,0,?,?,?,?,?,?)";
				PreparedStatement pstmtForInsert = conn.prepareStatement(updateLecture);
				pstmtForInsert.setString(1, l.getSubjectCode());
				pstmtForInsert.setString(2, l.getSubject());
				pstmtForInsert.setString(3, l.getMajor());
				pstmtForInsert.setString(4, l.getType());
				pstmtForInsert.setString(5, l.getGrade());
				pstmtForInsert.setInt(6, l.getMaxStudentNum());
				pstmtForInsert.setString(7, l.getTime());
				pstmtForInsert.setString(8, l.getRoom());
				switch (l.getTime().charAt(0)) {
				case '월':
					pstmtForInsert.setString(9, "1");
					break;
				case '화':
					pstmtForInsert.setString(9, "2");
					break;
				case '수':
					pstmtForInsert.setString(9, "3");
					break;
				case '목':
					pstmtForInsert.setString(9, "4");
					break;
				case '금':
					pstmtForInsert.setString(9, "5");
					break;
				}
				switch (l.getTime().charAt(2)) {
				case '1':
					pstmtForInsert.setString(10, "1");
					break;
				case '4':
					pstmtForInsert.setString(10, "4");
					break;
				case '7':
					pstmtForInsert.setString(10, "7");
					break;
				}
				pstmtForInsert.setString(11, "3");
				pstmtForInsert.setString(12, l.getPlan());
				pstmtForInsert.executeUpdate();

				String newRelation = "INSERT INTO open VALUES (?, ?)"; // 새로 생긴
																		// 행이랑
																		// 교수 관계
																		// 설정
				PreparedStatement pstmtForInsertOpen = conn.prepareStatement(newRelation);
				pstmtForInsertOpen.setString(1, l.getProfessor().getRegNo());
				pstmtForInsertOpen.setString(2, l.getSubjectCode());
				int a = pstmtForInsertOpen.executeUpdate();
				if(a >0) {
					conn.commit();
					return 1;
				} else {
					return 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return 0;
		} finally {
			closeAll();
		}
	}

	public ArrayList<Student> getStudentList(Lecture l) {
		ArrayList<Student> result = new ArrayList<>();
		makeConnection();
		String sql = "SELECT s_name, substr(s.s_regno, 2), s_major, s_grade, s_nowcredit FROM student s, register r "
				+ "WHERE s.s_regno = r.s_regno AND r.l_code = ? ORDER BY s_grade , s_name";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, l.getSubjectCode()); // 과목코드가 없는것 같다. 보내는 쪽에서 확인
			System.out.println(l.getSubjectCode());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(
						new Student(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5)));
			}
			l.setStudentList(result);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		System.out.println("DB> " + result);
		return result;
	}

	public boolean changePw(Human h) {
		boolean result = false;
		makeConnection();
		try {
			if (h instanceof Professor) {
				String sql = "UPDATE professor SET p_password = ? WHERE p_regno = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, h.getPw());
				pstmt.setString(2, h.getRegNo());
				pstmt.executeUpdate();
				result = true;
			} else if (h instanceof Student) {
				String sql = "UPDATE student SET s_password = ? WHERE s_regno = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, h.getPw());
				pstmt.setString(2, h.getRegNo());
				pstmt.executeUpdate();
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	
	/**
	 * 학생이 강의에 대해 검색할 때 사용한다
	 * 모든 조건에 대해 지정되었을때 지정되지 않았을 때를 구분하여 2^5가지 경우의 수를 갖는다.
	 * @param 찾고자 하는 조건을 가진 강의 객체
	 * @return
	 */
	public ArrayList<Lecture> searchLecture(Lecture l) {
		ArrayList<Lecture> lList = new ArrayList<>();
		makeConnection();
		String sql = "SELECT l.l_code, l_subject, l_major, l_type, l_grade, l_credit, l_maxstudent, p.p_name, l_time, l_room, l_maxstudent, l_nowstudent "
		+ "FROM lecture l, open o, professor p WHERE p.p_regno = o.p_regno AND l.l_code = o.l_code";
		
		boolean[] input = new boolean [5];
		
		if(l.getMajor().equals("전체")) input[0] = true;
		else input[0] = false;
		if(l.getGrade().equals("전체")) input[1] = true;
		else input[1] = false;
		if(l.getType().equals("전체")) input[2] = true;
		else input[2] = false;
		if(l.getProfessor().getName().equals("")) input[3] = true;
		else input[3] = false;
		if(l.getSubject().equals("")) input[4] = true;
		else input[4] = false;
		
		if(!input[0]) sql += " AND l_major = '" + l.getMajor() + "'";
		if(!input[1]) sql += " AND l_grade = '" + l.getGrade() + "'";
		if(!input[2]) sql += " AND l_type = '" + l.getType() + "'";
		if(!input[3]) sql += " AND p.p_name = '" + l.getProfessor().getName() + "'";
		if(!input[4]) sql += " AND l_subject like '%" + l.getSubject() + "%'";
		
		sql += " ORDER BY l_major, l_grade, l_subject";
		
		try{
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Lecture lec = new Lecture();
				lec.setSubjectCode(rs.getString(1));
				lec.setSubject(rs.getString(2));
				lec.setMajor(rs.getString(3));
				lec.setType(rs.getString(4));
				lec.setGrade(rs.getString(5));
				lec.setCredit(rs.getString(6));
				lec.setMaxStudentNum(rs.getInt(7));
				lec.setProfessor(new Professor(rs.getString(8), null, null));
				lec.setTime(rs.getString(9));
				lec.setRoom(rs.getString(10));
				lec.setMaxStudentNum(rs.getInt(11));
				lec.setNowStudentNum(rs.getInt(12));
				lList.add(lec);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		return lList;
	}
	
	
	/**
	 * 수강신청 처리하는 곳
	 * 해당 강의객체와 수강생을 받아서 등록한다.
	 * 이미 수강중인 강의라면 -1
	 * 수강학점 초과라면 -2
	 * 정원초과라면 -3
	 * 이미 수업 있는 시간이면 -4
	 * 수강신청 성공하면 1
	 * @param l
	 * @param s
	 * @return
	 */
	public int registerLecture(Lecture l,Student s){
		int result = 0;
		System.out.println("DB에서 빈자리?" + l.getVacantStudentNum());
		System.out.println(s.getRegNo()); 
		if(l.getVacantStudentNum() > 0){		//정원초과? (빈자리 > 0)
			if(s.getNowCredit()+3 <= 18){						//수강학점초과?
				makeConnection();
				try {
					System.out.println(l.getTime());
					System.out.println(s.getRegNo());
					String chkTime = "select l.l_code FROM lecture l, register r WHERE l_time = ? AND r.s_regno = ? AND l.l_code = r.l_code";
					pstmt = conn.prepareStatement(chkTime);
					pstmt.setString(1, l.getTime());
					pstmt.setString(2, s.getRegNo());
					rs = pstmt.executeQuery();
					if(!rs.next()){
						String chkRegister = "SELECT s_regno FROM register WHERE s_regno = ? AND l_code = ?";
						pstmt = conn.prepareStatement(chkRegister);
						pstmt.setString(1, s.getRegNo());
						pstmt.setString(2, l.getSubjectCode());
						rs = pstmt.executeQuery();
						if(!rs.next()){
							String sqlLecture = "UPDATE lecture SET l_nowstudent = (l_nowstudent + 1) WHERE l_code = ?";
							pstmt = conn.prepareStatement(sqlLecture);
							pstmt.setString(1, l.getSubjectCode());
							pstmt.executeUpdate();
							
							String sqlStudent = "UPDATE student SET s_nowcredit = (s_nowcredit +3) WHERE s_regno = ? ";
							pstmt = conn.prepareStatement(sqlStudent);
							pstmt.setString(1, s.getRegNo());
							pstmt.executeUpdate();
							
							String sqlRegister = "INSERT INTO register VALUES (?,?)";
							pstmt = conn.prepareStatement(sqlRegister);
							pstmt.setString(1, s.getRegNo());
							pstmt.setString(2, l.getSubjectCode());
							pstmt.executeUpdate();
							result = 1;
						}else {
							result = -1;
						}
					}else {
						result = -4;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					closeAll();
				}
			}else {
			result = -2;
			}
		}else {
			result  = -3;
		}
		return result;
	}
	
	
	public ArrayList<Lecture> getAllLectureForMyTable(Student s){
		ArrayList<Lecture> result = new ArrayList<>();
		makeConnection();
		try {
			String sql = "SELECT l.l_code, l_subject, l_major, l_type, l_grade, l_credit, l_maxstudent, p.p_name, l_time, l_room, l_maxstudent, l_nowstudent ,l_plan "
					+ "FROM lecture l, professor p, student s, register r, open o "
					+ "WHERE o.l_code = l.l_code AND o.p_regno = p.p_regno AND r.s_regno = s.s_regno AND r.l_code = l.l_code AND s.s_regno = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, s.getRegNo());
			rs = pstmt.executeQuery();
			while(rs.next()){
				Lecture lec = new Lecture();
				lec.setSubjectCode(rs.getString(1));
				lec.setSubject(rs.getString(2));
				lec.setMajor(rs.getString(3));
				lec.setType(rs.getString(4));
				lec.setGrade(rs.getString(5));
				lec.setCredit(rs.getString(6));
				lec.setMaxStudentNum(rs.getInt(7));
				lec.setProfessor(new Professor(rs.getString(8), null, null));
				lec.setTime(rs.getString(9));
				lec.setRoom(rs.getString(10));
				lec.setMaxStudentNum(rs.getInt(11));
				lec.setNowStudentNum(rs.getInt(12));
				lec.setPlan(rs.getString(13));
				result.add(lec);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		System.out.println(result);
		return result;
	}
	
	
	public int dropLecture(Student s, Lecture l){
		int result = 0;
		makeConnection();
		String sql = "DELETE register WHERE l_code = ? AND s_regno = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, l.getSubjectCode());
			pstmt.setString(2, s.getRegNo());
			int result1 = pstmt.executeUpdate();
			if(result1 > 0) result++;
			
			String updateL = "UPDATE lecture SET l_nowstudent = (l_nowstudent - 1) WHERE l_code = ?";
			pstmt = conn.prepareStatement(updateL);
			pstmt.setString(1, l.getSubjectCode());
			int result2 = pstmt.executeUpdate();
			if(result2 > 0) result++;
			
			String updateS = "UPDATE student SET s_nowcredit = (s_nowcredit - 3) WHERE s_regno = ?";
			pstmt = conn.prepareStatement(updateS);
			pstmt.setString(1, s.getRegNo());
			int result3 = pstmt.executeUpdate();
			if(result3 > 0) result++;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public int getCredit(Student s){
		int result = 0;
		makeConnection();
		String sql = "SELECT s_nowcredit FROM student WHERE s_regno = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, s.getRegNo());
			rs = pstmt.executeQuery();
			if(rs.next()){
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	/**
	 * 해당 교수의 전체 강의목록을 arrayList로 반환
	 * 
	 * @param p
	 * @return
	 */
	public ArrayList<Lecture> getAllLecture(Student s) {
		ArrayList<Lecture> result = new ArrayList<>();
		makeConnection();
		try {
			String sql = "SELECT l.l_code, l_subject, l_major, l_type, l_grade, l_maxstudent, l_nowstudent, l_time, l_room, l_time_index, l_module "
					+ "FROM lecture l, register r WHERE r.s_regno = ? AND l.l_code = r.l_code ORDER BY substr(l_time, 2), l_time_index";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, s.getRegNo());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Lecture l = new Lecture();
				l.setSubjectCode(rs.getString(1));
				l.setSubject(rs.getString(2));
				l.setMajor(rs.getString(3));
				l.setType(rs.getString(4));
				l.setGrade(rs.getString(5));
				l.setMaxStudentNum(rs.getInt(6));
				l.setNowStudentNum(rs.getInt(7));
				l.setTime(rs.getString(8));
				l.setRoom(rs.getString(9));
				l.setTime_index(rs.getString(10));
				l.setModule(rs.getString(11));
				result.add(l);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		System.out.println("ServerDB> 해당교수 전체 강의목록 검색후 리턴완료");
		return result;
	}
	
	
	public Lecture getLectureWithPlan(Lecture l){
		makeConnection();
		String sql = "SELECT l_plan FROM lecture WHERE l_code = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, l.getSubjectCode());
			rs = pstmt.executeQuery();
			if(rs.next()){
				l.setPlan(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
		return l;
	}
	
	

}// class
