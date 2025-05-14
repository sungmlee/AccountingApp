import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class App {
	private final String ID = "Danny";
	private final String PASS = "abc";
	private JFrame frame;
	private JTextField idField;
	private JPasswordField passField;
	private JPanel currPanel;
	private JTextField nameInput;
	private JTextField amountInput;
	private JTextField searchInput;
	private JTable table;
	private JComboBox typeInput;
	private List<String> categoryList = new ArrayList<>(); // 카테고리 목록
	private int loggedInUserId;  // 로그인한 사용자의 ID

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App window = new App();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public App() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		TableData td = new TableData(loggedInUserId);
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImagePanel loginPanel = new ImagePanel(new ImageIcon("/Users/sungmin/Desktop/Maven/Maven_AccountingApp/src/main/resources/theme.jpg").getImage());

		currPanel = loginPanel;
		ImagePanel tranPanel = new ImagePanel(new ImageIcon("/Users/sungmin/Desktop/Maven/Maven_AccountingApp/src/main/resources/Activation.jpg").getImage());

		frame.setSize(loginPanel.getDim());
		frame.setPreferredSize(loginPanel.getDim());
		ImagePanel sumPanel = new ImagePanel(new ImageIcon("/Users/sungmin/Desktop/Maven/Maven_AccountingApp/src/main/resources/Activation.jpg").getImage());
		frame.getContentPane().add(sumPanel);

		sumPanel.setVisible(false);

		// Summary

		JButton tranBtn = new JButton("");
		tranBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currPanel.setVisible(false);
				tranPanel.setVisible(true);
				currPanel = tranPanel;
			}
		});
		tranBtn.setIcon(new ImageIcon("/Users/sungmin/Desktop/Maven/Maven_AccountingApp/src/main/resources/Transaction.jpg"));
		tranBtn.setBounds(29, 182, 259, 40);
		tranBtn.setBorder(null);
		sumPanel.add(tranBtn);

		JLabel lblSearch = new JLabel("Search :");
		lblSearch.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblSearch.setBounds(337, 76, 83, 40);
		sumPanel.add(lblSearch);

		searchInput = new JTextField();
		searchInput.setFont(new Font("Tahoma", Font.PLAIN, 22));
		searchInput.setBounds(432, 76, 1080, 40);
		sumPanel.add(searchInput);
		searchInput.setColumns(10);

		JPanel tp = new JPanel();
		tp.setBounds(337, 140, 1175, 467);
		sumPanel.add(tp);

		table = new JTable(td);
		table.setBounds(337, 140, 1155, 445);
		table.setRowHeight(30);
		table.setFont(new Font("Sansserif", Font.BOLD, 15));
		table.setPreferredScrollableViewportSize(new Dimension(1155, 430));
		tp.add(new JScrollPane(table));
		tp.setOpaque(false);

		JTableHeader header = table.getTableHeader();
		header.setBackground(new Color(92, 179, 255));
		header.setForeground(new Color(255, 255, 255));
		header.setFont(new Font("Sansserif", Font.BOLD, 15));

		searchInput.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				String search = searchInput.getText();
				TableRowSorter<AbstractTableModel> trs = new TableRowSorter<>(td);
				table.setRowSorter(trs);
				trs.setRowFilter(RowFilter.regexFilter(search));
			}
		});

		frame.getContentPane().add(tranPanel);

		tranPanel.setVisible(false);
		frame.getContentPane().add(loginPanel);

		idField = new JTextField();
		idField.setFont(new Font("Tahoma", Font.PLAIN, 26));
		idField.setBounds(1223, 311, 296, 43);
		loginPanel.add(idField);
		idField.setColumns(10);
		idField.setBorder(null);

		passField = new JPasswordField();
		passField.setFont(new Font("Tahoma", Font.PLAIN, 26));
		passField.setBounds(1223, 391, 296, 43);
		passField.setBorder(null);
		loginPanel.add(passField);

		JCheckBox chckbxNewCheckBox = new JCheckBox("");
		chckbxNewCheckBox.setBounds(1184, 440, 25, 25);
		loginPanel.add(chckbxNewCheckBox);

		JButton logInBtn = new JButton("");
		logInBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String inputId = idField.getText();
				String inputPass = new String(passField.getPassword());
				String hashedPass = hashPassword(inputPass);

				try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
					 PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
					ps.setString(1, inputId);
					ps.setString(2, hashedPass);


					ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						loggedInUserId = rs.getInt("id");  // userId 저장
						currPanel.setVisible(false);
						sumPanel.setVisible(true);
						currPanel = sumPanel;
					} else {
						JOptionPane.showMessageDialog(null, "로그인 실패: 아이디 또는 비밀번호가 틀렸습니다.");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "DB 오류: " + ex.getMessage());
				}
			}
		});

		logInBtn.setBorder(null);
		logInBtn.setBounds(1183, 467, 338, 38);
		loginPanel.add(logInBtn);
		logInBtn.setIcon(new ImageIcon("/Users/sungmin/Desktop/Maven/Maven_AccountingApp/src/main/resources/button.jpg"));
		logInBtn.setPressedIcon(new ImageIcon("/Users/sungmin/Desktop/Maven/Maven_AccountingApp/src/main/resources/btnClicked.jpg"));

		JButton signupBtn = new JButton("회원가입");
		signupBtn.setBounds(1183, 520, 338, 38); // 위치 조정
		loginPanel.add(signupBtn);
		signupBtn.addActionListener(e -> showSignupPanel());


		// Transaction

		JButton sumBtn = new JButton("");
		sumBtn.setIcon(new ImageIcon("/Users/sungmin/Desktop/Maven/Maven_AccountingApp/src/main/resources/Summary.jpg"));
		sumBtn.setBorder(null);
		sumBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currPanel.setVisible(false);
				sumPanel.setVisible(true);
				currPanel = sumPanel;
			}
		});
		sumBtn.setBounds(29, 123, 259, 40);
		tranPanel.add(sumBtn);

		JLabel lblName = new JLabel("Name");
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblName.setBounds(378, 123, 139, 49);
		tranPanel.add(lblName);

		JLabel lblType = new JLabel("Type");
		lblType.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblType.setBounds(378, 203, 139, 49);
		tranPanel.add(lblType);

		JLabel lblAmount = new JLabel("Amount");
		lblAmount.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblAmount.setBounds(378, 284, 139, 49);
		tranPanel.add(lblAmount);

		JLabel lblNote = new JLabel("Note");
		lblNote.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblNote.setBounds(378, 370, 139, 49);
		tranPanel.add(lblNote);

		nameInput = new JTextField();
		nameInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
		nameInput.setBounds(527, 123, 935, 49);
		tranPanel.add(nameInput);
		nameInput.setColumns(10);

		typeInput = new JComboBox();
		loadExpenseCategories(); // 카테고리 로딩
		typeInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
		typeInput.setBounds(527, 203, 935, 49);
		tranPanel.add(typeInput);
		typeInput.setBackground(Color.WHITE);

		amountInput = new JTextField();
		amountInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
		amountInput.setColumns(10);
		amountInput.setBounds(527, 284, 935, 49);
		tranPanel.add(amountInput);

		JTextArea noteInput = new JTextArea();
		noteInput.setFont(new Font("Courier New", Font.PLAIN, 33));
		noteInput.setBounds(527, 370, 935, 60);
		tranPanel.add(noteInput);
		noteInput.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		// 날짜 입력 라벨
		JLabel lblDate = new JLabel("Date");
		lblDate.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblDate.setBounds(378, 490, 139, 49);
		tranPanel.add(lblDate);

		// 날짜 선택 스피너
		SpinnerDateModel dateModel = new SpinnerDateModel();
		JSpinner dateInput = new JSpinner(dateModel);
		JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateInput, "yyyy-MM-dd");
		dateInput.setEditor(dateEditor);
		dateInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
		dateInput.setBounds(527, 490, 300, 49);
		tranPanel.add(dateInput);

		// 시간 입력 라벨
		JLabel lblTime = new JLabel("Time");
		lblTime.setFont(new Font("Tahoma", Font.PLAIN, 33));
		lblTime.setBounds(378, 550, 139, 49);
		tranPanel.add(lblTime);

		// 시간 선택 스피너
		SpinnerDateModel timeModel = new SpinnerDateModel();
		JSpinner timeInput = new JSpinner(timeModel);
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeInput, "HH:mm:ss");
		timeInput.setEditor(timeEditor);
		timeInput.setFont(new Font("Tahoma", Font.PLAIN, 33));
		timeInput.setBounds(527, 550, 300, 49);
		tranPanel.add(timeInput);

		JButton btnNewButton = new JButton("SUBMIT");

		// 거래 추가 버튼의 액션 리스너 수정
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String name = nameInput.getText();
				String type = (String) typeInput.getSelectedItem();
				String amount = amountInput.getText();
				String note = noteInput.getText();

				// 날짜와 시간 가져오기
				java.util.Date selectedDate = (java.util.Date) dateInput.getValue();
				java.util.Date selectedTime = (java.util.Date) timeInput.getValue();

				LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalTime localTime = selectedTime.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
				LocalDateTime dateTime = LocalDateTime.of(localDate, localTime);
				Timestamp timestamp = Timestamp.valueOf(dateTime);

				int categoryId = -1;

				try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234")) {
					try (PreparedStatement catStmt = conn.prepareStatement("SELECT id FROM expense_categories WHERE name = ?")) {
						catStmt.setString(1, type);
						try (ResultSet catRs = catStmt.executeQuery()) {
							if (catRs.next()) {
								categoryId = catRs.getInt("id");
							} else {
								JOptionPane.showMessageDialog(null, "해당 카테고리가 없습니다.");
								return;
							}
						}
					}

					try (PreparedStatement ps = conn.prepareStatement("INSERT INTO expenses (user_id, category_id, amount, description, date) VALUES (?, ?, ?, ?, ?)")) {
						ps.setInt(1, loggedInUserId);
						ps.setInt(2, categoryId);
						ps.setBigDecimal(3, new BigDecimal(amount));
						ps.setString(4, note);
						ps.setTimestamp(5, timestamp);
						ps.executeUpdate();
						JOptionPane.showMessageDialog(null, "거래가 추가되었습니다.");
					}
				} catch (SQLException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "DB 오류: " + e.getMessage());
				}
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 33));
		btnNewButton.setBounds(527, 572, 935, 71);
		tranPanel.add(btnNewButton);
	}

	// 패스워드 해싱 메서드
	private String hashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : hashedBytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 카테고리 로드 메서드
	private void loadExpenseCategories() {
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT * FROM expense_categories")) {
			while (rs.next()) {
				categoryList.add(rs.getString("name"));
			}
			typeInput.setModel(new DefaultComboBoxModel<>(categoryList.toArray(new String[0])));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void showSignupPanel() {
		JFrame signupFrame = new JFrame("회원가입");
		signupFrame.setSize(400, 300);
		signupFrame.setLayout(null);

		JLabel userLabel = new JLabel("아이디:");
		userLabel.setBounds(50, 50, 80, 25);
		signupFrame.add(userLabel);

		JTextField userText = new JTextField();
		userText.setBounds(150, 50, 160, 25);
		signupFrame.add(userText);

		JLabel passwordLabel = new JLabel("비밀번호:");
		passwordLabel.setBounds(50, 100, 80, 25);
		signupFrame.add(passwordLabel);

		JPasswordField passwordText = new JPasswordField();
		passwordText.setBounds(150, 100, 160, 25);
		signupFrame.add(passwordText);

		JButton signupButton = new JButton("가입하기");
		signupButton.setBounds(150, 150, 100, 30);
		signupFrame.add(signupButton);

		signupButton.addActionListener(e -> {
			String username = userText.getText();
			String password = new String(passwordText.getPassword());
			String hashedPassword = hashPassword(password);

			try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
				 PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
				ps.setString(1, username);
				ps.setString(2, hashedPassword);
				ps.executeUpdate();
				JOptionPane.showMessageDialog(signupFrame, "회원가입 성공!");
				signupFrame.dispose();
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(signupFrame, "회원가입 실패: " + ex.getMessage());
			}
		});

		signupFrame.setVisible(true);
	}


	// 월별 요약 업데이트 메서드
	private void updateMonthlySummary(String type, double amount) {
		String month = java.time.LocalDate.now().getMonth().toString();
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountbook", "root", "1234");
			 PreparedStatement ps = conn.prepareStatement(
					 "INSERT INTO monthly_summary (month, category, total_amount) VALUES (?, ?, ?) " +
							 "ON DUPLICATE KEY UPDATE total_amount = total_amount + ?")) {
			ps.setString(1, month);
			ps.setString(2, type);
			ps.setDouble(3, amount);
			ps.setDouble(4, amount);

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
