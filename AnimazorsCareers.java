package com.JDBCLearn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class AnimazorsCareers {
	
	private final static String url = "jdbc:mysql://localhost:3306/database_name";
	private final static String userName= "DB_UserName";
	private final static String pwd = "DB_Password";
	
	static Scanner scan = new Scanner(System.in);
	
	public static void main(String[] args) {

		try {
		
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url,userName,pwd);
			operationsCURD(con);

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
	}

	private static void operationsCURD(Connection con) {

		try {			
			int n = 0;
			while(n != 6) {
				System.out.println("1.Hire Employee\n"
								 + "2.Show Employees\n"
								 + "3.Fire an Employee\n"
								 + "4.Change Employee Salary or Role\n"
								 + "5.Print Employee with specific id\n"
								 + "6.Exit");
				n = scan.nextInt();
				
				switch(n) {
				case 1:
					hireEmployee(con);
					break;
				case 2:
					showEmployees(con);
					break;
				case 3:
					fireEmployee(con);
					break;
				case 4:
					alterEmployeeDetails(con);					
					break;
				case 5:
					printEmployeeWithId(con);
					break;
				case 6:
					System.out.println("<-----------Exited----------->");
					System.exit(0);
				default:
					System.out.println("Enter a valid input");
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private static void hireEmployee(Connection con) {
		
		System.out.println("<-----------Hire an Employee----------->");
		try {
			String query = "insert into employees(name, role, salary) values (?,?,?)";
			PreparedStatement pst = con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
			
			//Getting input from the user
			System.out.print("Enter Emplyee name : ");
			scan.nextLine();
			String name = scan.nextLine();
			System.out.print("Enter Emplyee role: ");
			String role = scan.nextLine();
			System.out.print("Enter Emplyee salary: ");
			int salary = scan.nextInt();
			
			//Setting values to the prepared statement
			pst.setString(1, name);
			pst.setString(2, role);
			pst.setInt(3, salary);
			pst.execute();
			
			//To get lastly inserted row's Employee id(Auto-generated in database)
			ResultSet res = pst.getGeneratedKeys();
			int id = 0;
			if(res.next()) {
				id = res.getInt(1);
			}
			
			//Printing the lastly inserted row
			query = "select * from employees where id = "+id;
			Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, 0);
			res = st.executeQuery(query);
			System.out.println("<--------------------------->");
			System.out.println("Employee hired(Query Inserted). here are the details from the database");
			printResult(res);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}		
		
	}
	
	private static void showEmployees(Connection con) {
		
		System.out.println("<-----------All Employee Details----------->");
		try {
			
			String query = "select * from employees";
			Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,0);
			ResultSet res = st.executeQuery(query);
			
			printResult(res);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
	}

	private static void fireEmployee(Connection con) {

		System.out.println("<-----------Fire an Employee----------->");
		try {
			
			Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,0);

			System.out.print("\nWho do you want to fire ?\n\nEnter the Employee Id : ");
			int n = scan.nextInt();
			
			String query = "select * from employees where id = " + n;
			ResultSet res = st.executeQuery(query);
			
			//Checking whether the Employee exist or not in database
			if( !(res.next())) {
				System.out.println("There is no such Employee with the id - " + n);
				System.out.println("<--------------------------->");
				return;
			}
			res.previous();
			
			
			query = "delete from employees where id = " + n;
			
			//Before deleting printing the user
			if(res.next()) {				
				System.out.println("Employee " + res.getString("name") + " has been fired.");	
			}
			System.out.println("<--------------------------->");
			
			//Deleting the Employee from the table
			st.executeUpdate(query);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void alterEmployeeDetails(Connection con) {

		System.out.println("<-----------Changing Employee Salary or Role----------->");
		try {
			
			int c = 0;
			while(c != 3) {
				System.out.println("What to change\n1.Role\n2.Salary\n3.Don't change exit");
				c = scan.nextInt();
				
				switch(c) {
				case 1:
					changeEmployeeRole(con);
					break;
				case 2:
					changeEmployeeSalary(con);
					break;
				case 3:
					System.out.println("<-----------Exited For Changing----------->");
					break;
				default:
					System.out.println("Enter a vaild input");
					break;
				}
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
		
	private static void changeEmployeeRole(Connection con) {

		System.out.println("<-----------Changing Employee Role----------->");
		try {
			String UpdateQurey = "update employees set role = ? where id = ?";
			PreparedStatement pst = con.prepareStatement(UpdateQurey);
			
			System.out.print("\nTo whom the role want to be updated ?\n\nEnter the Employee id : ");
			int id = scan.nextInt();
			String selectQuery = "select * from employees where id = " + id;
			
//		<----Checking whether the user Exist or not---->
			Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,0);
			ResultSet res = st.executeQuery(selectQuery);
			
			if( !(res.next())) {
				System.out.println("There is no such Employee with the id - " + id);
				System.out.println("<--------------------------->");
				return;
			}
			res.previous();
//		<----------------------------------------------->
			
//		<----Setting new role for employee---->
			System.out.println("\nEnter the new role : ");
			scan.nextLine();
			String role = scan.nextLine();
			pst.setString(1, role);
			pst.setInt(2, id);
			pst.executeUpdate();
//		<----------------------------------------------->
			
//		<----Printing the updated Result---->
			System.out.println("<-----------Employee role updated----------->");
			res = st.executeQuery(selectQuery);
			printResult(res);
//		<----------------------------------------------->
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	private static void changeEmployeeSalary(Connection con) {
		
		System.out.println("<-----------Changing Employee Salary----------->");
		try {
			String UpdateQurey = "update employees set salary = ? where id = ?";
			PreparedStatement pst = con.prepareStatement(UpdateQurey);
			
			System.out.print("\nTo whom the salary want to be updated ?\n\nEnter the Employee id : ");
			int id = scan.nextInt();
			String selectQuery = "select * from employees where id = " + id;
			
//		<----Checking whether the user Exist or not---->
			Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,0);
			ResultSet res = st.executeQuery(selectQuery);
			
			if( !(res.next())) {
				System.out.println("There is no such Employee with the id - " + id);
				System.out.println("<--------------------------->");
				return;
			}
			res.previous();
//		<----------------------------------------------->
			
//		<----Setting new salary for employee---->
			System.out.println("\nEnter the new salary : ");
			int salary= scan.nextInt();
			pst.setInt(1, salary);
			pst.setInt(2, id);
			pst.executeUpdate();
//		<----------------------------------------------->
			
//		<----Printing the updated Result---->
			System.out.println("<-----------Employee role updated----------->");
			res = st.executeQuery(selectQuery);
			printResult(res);
//		<----------------------------------------------->
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	//It will print All the row present in the table
	private static void printResult(ResultSet res) {
		try {
		
			//Checking whether the ResultSet has Data or Not
			if( !(res.next())) {
				System.out.println("There is no data to be printed");
				System.out.println("<--------------------------->");
				return;
			}
			res.previous();
			
			//While coding we don't what are the and how many fields that
			//are present in database. So we use Metadata to gather the data about data.
			ResultSetMetaData metaData = res.getMetaData();
			
			//It will give the No of Columns present in the table.
			int n = metaData.getColumnCount();

			while(res.next()) {
				for(int i = 1; i<=n; i++) {
					//It will give the Column name present in the i'th position in the table.
					String columnName = metaData.getColumnName(i);
					System.out.println(columnName.substring(0, 1).toUpperCase() + columnName.substring(1) + 
							" : " + res.getObject(columnName));
				}
				System.out.println("<--------------------------->");
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}		
	}

	//It will print only one row with specific ID present in the table
	private static void printEmployeeWithId(Connection con) {
		
		try {
			
			System.out.println("<-----------Employee with specific id----------->");
			System.out.println("\nEnter Employee id : ");
			int id = scan.nextInt();
			
			String selectQuery = "select * from employees where id = "+id;
			Statement st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,0);
			ResultSet res = st.executeQuery(selectQuery);
			
			//Checking whether the ResultSet has Data or Not
			if( !(res.next())) {
				System.out.println("There is no such Employee with the id - " + id);
				System.out.println("<--------------------------->");
				return;
			}
			res.previous();
			
			printResult(res);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
