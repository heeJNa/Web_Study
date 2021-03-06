package com.sist.dao;
// DAO vs Service
import java.util.*;
import java.sql.*;
public class FoodService {
	// 연결 객체 => 한번만 사용
		private Connection conn;
		private PreparedStatement ps;
		private final String URL="jdbc:oracle:thin:@211.63.89.131:1521:XE";
		
		//1 .드라이버 등록 => 싱글턴(한번만 호출 => 생성자)
		public FoodService(){
			// TODO Auto-generated constructor stub
		
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				// 클래스의 정보를 읽어 온다 (리플렉션)
				// 클래스 메모리 활동
				// 메소드 제어, 멤버변수 제어, 생성자 ... => 스프링 (클래스 관리)
				// 등록시에 반드시 (패키지.클래스명)
			}catch(ClassNotFoundException e)	{
				e.printStackTrace();
			}
		}
		public void getConnection(){
	        try {
	            conn = DriverManager.getConnection(URL,"hr","happy");
	        }catch (SQLException e){
	        	System.out.println("DB연동 실패");
	            e.printStackTrace();
	        }
	    }

	    public void disConnection() {
	        try{
	            if(ps != null) ps.close();
	            if(conn != null)conn.close();
	        }catch (Exception e){
	            e.printStackTrace();
	        }
	    }
	    // 4. 기능
	    // 4-1. => 카테고리 출력
	    public List<CategoryVO> categoryData(){
	    	List<CategoryVO> list = new ArrayList<CategoryVO>();
	    	try {
	    		// 1. 연결
	    		getConnection();
	    		// 2. SQL문장
	    		String sql="SELECT cno,title,poster,subject "
	    				+ "FROM category "
	    				+ "ORDER BY cno";
	    		// 3. 오라클로 SQL문장 전송
	    		ps = conn.prepareStatement(sql);
	    		// 4. ? => 값을 채운다
	    		// 5. SQL실행 결과값 저장
	    		ResultSet rs = ps.executeQuery();
	    		// 6. 저장된 위치 => 데이터 읽기 => List
	    		while(rs.next()) {
	    			CategoryVO vo = new CategoryVO();
	    			
	    			vo.setCno(rs.getInt(1));
	    			vo.setTitle(rs.getString(2));
	    			vo.setPoster(rs.getString(3));
	    			vo.setSubject(rs.getString(4));
	    			
	    			list.add(vo);
	    		}
	    		rs.close();
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}finally {
				disConnection();
			}
	    	
	    	return list;
	    }
	    // 4-2. => 카테고리별 맛집 목록
	    public List<FoodVO> categoryListData(int cno){
	    	List<FoodVO> list = new ArrayList<FoodVO>();
	    	try {
	    		// 1. 연결
	    		getConnection();
	    		// 2. SQL문장
	    		String sql="SELECT no,name,address,tel,type,poster,score "
	    				+ "FROM foodhouse "
	    				+ "WHERE cno=?";
	    		// 3. 오라클로 SQL문장 전송
	    		ps=conn.prepareStatement(sql);
	    		// 4. ? => 값을 채운다
	    		ps.setInt(1, cno);
	    		// 5. SQL실행 결과값 저장
	    		ResultSet rs = ps.executeQuery();
	    		// 6. 저장된 위치 => 데이터 읽기 => List
	    		while(rs.next()) {
	    			FoodVO vo = new  FoodVO();
	    			
	    			vo.setNo(rs.getInt(1));
	    			
	    			vo.setName(rs.getString(2));
	    			String address = rs.getString(3);
//	    			address = address.substring(0,address.lastIndexOf("지")).trim();
	    			vo.setAddress(address);
	    			vo.setTel(rs.getString(4));
	    			vo.setType(rs.getString(5));
	    			String poster = rs.getString(6);
	    			poster = poster.substring(0,poster.indexOf("^"));
	    			poster = poster.replace("#", "&");
	    			vo.setPoster(poster);
	    			vo.setScore(rs.getDouble(7));
	    			
	    			list.add(vo);
	    		}
	    		rs.close();
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}finally {
				disConnection();
			}
	    	return list;
	    }
	    // 4-3. => 카테고리 정보 출력
	    public CategoryVO categoryInfoData(int cno) {
	    	CategoryVO vo = new CategoryVO();
	    	try {
	    		// 1. 연결
	    		getConnection();
	    		// 2. SQL문장
	    		String sql="SELECT title,subject "
	    				+ "FROM category "
	    				+ "WHERE cno=?";
	    		// 3. 오라클로 SQL문장 전송
	    		ps= conn.prepareStatement(sql);
	    		// 4. ? => 값을 채운다
	    		ps.setInt(1, cno);
	    		// 5. SQL실행 결과값 저장
	    		ResultSet rs = ps.executeQuery();
	    		// 6. 저장된 위치 => 데이터 읽기 => List
	    		rs.next();
	    		vo.setTitle(rs.getString(1));
	    		vo.setSubject(rs.getString(2));
	    		rs.close();
	    		
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}finally {
				disConnection();
			}
	    	return vo;
	    }
	    
	    // 웹 => 사용자 (선택, 클릭) => 클릭한 정보(데이터 =>
//	    								톰캣(request)	
	    // 오라클 연결 => 데이터 출력
	    // JSP ==> 오라클 ==> JSP
		/* 
		 * 		request는 파일이 바뀌면 초기화된다.
		 * 		 
		 * 		JSP
		 * 		----
		 * 		basic
		 * 			사용법 (동작방법)
		 * 			 자바 / HTML
		 * 			------------ 구분
		 * 			<% %> , <%= %> , <%! %> , <%-- --%>
		 * 			-----------------------------------
		 * 			코딩이 영역을 벗어나면 HTML로 인식
		 * 			*** 브라우저에서는 자바는 일반 텍스트
		 * 			지시자 :
		 * 				page : contentType = 변환
		 * 						=> JSP가 실행이되면 메모리에 저장
		 * 						=> 브라우저 (HTML/XML/JSON)
		 * 						=> HTML : text/html
		 * 						=> XML  : text/xml
		 * 						=> JSON : text/plain
		 * 						=> 한글 (인코딩=> byte형식)
		 * 							charset = UTF-8 / EUC-KR
		 * 						import => 자바에서 필요한 라이브러리
		 * 									사용자 정의 클래스를 불러올때 사용	
		 * 						errorPage => 에러가 날 경우 이동하는 페이지
		 * 						isErrorPage => 종류별 처리
		 * 							(404,403,412,400,500)
		 * 								=> 시스템에서 저장하고 있는 에러파일 전송
		 * 						=> 사용자 (한번도 사용해보지 않은 사람을 대상)
		 * 						
		 * 				taglib : <% %>를 사용하지 않고 자바 코딩 => 태그혈
		 * 				----------------- 실무에서는 무조건 (95% => MVC)
		 * 									자바 / HTML을 분리해서 사용
		 * 									----  -----
		 * 									Back  Front (Spring은 MVC만 존재)
		 * 									=> 화면 출력
		 * 										제어문, 변수선언, URL이동, 날짜 변경	
		 * 				include : 조립식 프로그램 (CBD)
		 * 							jsp한개를 컴포넌트 ==> main페이지
		 * 			내장객체 : 미리 만들어서 사용이 가능
		 * 				------------------------
		 * 				request	
		 * 				response
		 * 				session
		 * 				application
		 * 				pageContext 
		 * 				------------------------ MVC,Spring에서 사용
		 * 				out => <%= %> , ${}
		 * 				config => web.xml(Spring 동작)
		 * 				exception => try{}catch()
		 * 				page => this
		 * 
		 * 			JSP에서 제공하는 액션 태그
		 * 				<jsp:useBean>
		 * 				<jsp:setProperty>
		 * 				<jsp:include>
		 * 
		 * 			에러처리 
		 * 			cookie 사용
		 * 		middle
		 * 			-----------------------
		 * 			JSTL / EL
		 * 			servlet / MVC
		 * 			XML / Annotation
		 * 			----------------------- 라이브러리 제작 (스프링)
		 * 
		 * */
	    
	    // 4-4. => 맛집 상세보기
	 		/*
	 		 * private int no, cno; 
	 		 * private String poster,name,address,tel,type,price,time,menu,parking; 
	 		 * private double score;
	 		 */
	    public FoodVO foodDetailData(int no) {
	    	FoodVO vo = new FoodVO();
	    	try {
	    		// 1. 연결
	    		getConnection();
	    		// 2. SQL문장
	    		String sql = "SELECT no,cno,name,score,poster,address,"
	    				+ "tel,type,price,time,parking,menu "
	    				+ "FROM foodhouse "
	    				+ "WHERE no=?";
	    		// 3. 오라클로 SQL문장 전송
	    		ps = conn.prepareStatement(sql);
	    		// 4. ? => 값을 채운다
	    		ps.setInt(1, no);
	    		// 5. SQL실행 결과값 저장
	    		ResultSet rs = ps.executeQuery();
	    		// 6. 저장된 위치 => 데이터 읽기 => List
	    		rs.next(); // 메모리의 데이터 출력 위치에 커서이동
	    		// 7. FoodVO에 값을 채운다.
	    		vo.setNo(rs.getInt(1));
	    		vo.setCno(rs.getInt(2));
	    		vo.setName(rs.getString(3));
	    		vo.setScore(rs.getDouble(4));
	    		vo.setPoster(rs.getString(5));
	    		vo.setAddress(rs.getString(6));
	    		vo.setTel(rs.getString(7));
	    		vo.setType(rs.getString(8));
	    		vo.setPrice(rs.getString(9));
	    		vo.setTime(rs.getString(10));
	    		vo.setParking(rs.getString(11));
	    		vo.setMenu(rs.getString(12));;
	    		
	    		rs.close();
	    	}catch(Exception e) {
	    		e.printStackTrace();
	    	}finally {
				disConnection();
			}
	    	return vo;
	    }
}
