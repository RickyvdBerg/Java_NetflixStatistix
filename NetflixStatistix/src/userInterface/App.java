package userInterface;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import database.DatabaseHandler;

import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;


import java.awt.GridLayout;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import javax.swing.SpinnerNumberModel;
import java.awt.CardLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import net.miginfocom.swing.MigLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class App implements Runnable{

	private DatabaseHandler dbHandler;
	
	private JFrame frmNetflixStatistix;
	
	private JTextField tfAccountName;
	private JTextField tfAccountStreet;
	private JTextField tfAccountZip;
	private JTextField tfProfileName;
	private JTextField tfAccountCity;
	private JTextField tfAccountHouseNr;
	
	private JSpinner spinAccountID;
	private JSpinner spinProfileDay;
	private JSpinner spinProfileMonth;
	private JSpinner spinProfileYear;
	private JSpinner spinProfileSubscriberID;
	private JSpinner spinProfileID;
	private JSpinner spinWatchedID;
	private JSpinner spinWatchedPercentage;
	
	private JComboBox cbOverview1Serie; 
	private JComboBox cbOverview2Serie; 
	private JComboBox cbOverview2Subscriber; 
	private JComboBox cbOverview3Subscriber;
	private JComboBox cbOverview6Movie;
	private JComboBox cbWatchedProgram;
	private JComboBox cbWatchedProfile;
	private JComboBox cbWatchedType;
	private JComboBox cbWatchedSubscriber;
	private JComboBox cbWatchedEpisode;
	
	private final ButtonGroup btnGroupAccount = new ButtonGroup();
	private final ButtonGroup btnGroupProfile = new ButtonGroup();
	private final ButtonGroup btnGroupWatched = new ButtonGroup();

	private HashMap<Integer, String> mapSerieTitles;
	private HashMap<Integer, String> mapSubscriberNames;
	private HashMap<Integer, String> mapMovieTitles;
	private HashMap<Integer, String> mapProfiles;
	private HashMap<Integer, String> mapEpisodes;
	
	private JTable tableAccount;
	private JTable tableProfile;
	private JTable tableWatched;
	private JTable tableOverview1;
	private JTable tableOverview2;
	private JTable tableOverview5;
	private JTable tableOverview3;
	private JTable tableOverview4;
	private JTable tableOverview6;

	//constructor initializes the databasehandler
	//runs the fill creating the entire application
	public App() {
		dbHandler = new DatabaseHandler();
		fillForm();
	}
	
	//try and run the application
	public void run() {
		try {
			App window = new App();
			window.frmNetflixStatistix.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//fill the frame with content
	private void fillForm() {
		//---------------------------------------------------------------------------------------------------------------------------------------------
		//Creating the main frame
		//---------------------------------------------------------------------------------------------------------------------------------------------
		frmNetflixStatistix = new JFrame();
		frmNetflixStatistix.setTitle("Netflix Statistix");
		//Adding a listener to when the window opened and presented to the screen
		frmNetflixStatistix.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				//When user loads the app some info should be fetched from the server
				//like the main gridview in the first screen
				tableAccount.setModel(dbHandler.execute("SELECT * FROM Subscriber"));
				
				//also the comboboxes in the program should have their content populated with info from the database.
				mapSerieTitles = dbHandler.executeToMap("SELECT Serie.Id, Serie.Title FROM Serie");
				mapMovieTitles = dbHandler.executeToMap("SELECT Movie.Id, Movie.Title FROM Movie");
				
				//It's nice to fill the subscriber list beforehand but it should be noted that you can add subscribers in the program
				//so leaving this line only here will not be enough. Since it only gets executed at the start and not when the user adds a new subscriber
				mapSubscriberNames = dbHandler.executeToMap("SELECT Subscriber.Id, Subscriber.FullName FROM Subscriber");
			}
		});
		frmNetflixStatistix.setBounds(100, 100, 778, 484);
		frmNetflixStatistix.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmNetflixStatistix.getContentPane().setLayout(new BorderLayout(0, 0));
		//---------------------------------------------------------------------------------------------------------------------------------------------
		//Adding a tabbed panel to hold all the overviews etc.
		//---------------------------------------------------------------------------------------------------------------------------------------------
		//panel should be alligned on the left side.
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		
		//Adding a listener to the tabs to see if the user changed tabs
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				String query;
				//Switch case for the tabs, if user changed to tab 1 do this etc.
				switch(tabbedPane.getSelectedIndex())
				{
				//user clicks on tab profile, the profile gridview should now show all profiles
				case 1:
					tableProfile.setModel(dbHandler.execute("SELECT * FROM Profile"));
					break;
				//user clicks on tab Watched, the profile gridview should now show all watched programs
					//also the comboboxes should be filled with defaults
				case 2:
					tableWatched.setModel(dbHandler.execute("SELECT * FROM Watched"));
					cbWatchedProgram.setModel(new DefaultComboBoxModel(mapSerieTitles.values().toArray()));
					query = "SELECT Episode.Id, Episode.Title" + 
							" FROM Episode WHERE SerieId = " + getKeyFromValue(mapSerieTitles, cbWatchedProgram.getSelectedItem().toString()) ;
					mapEpisodes = dbHandler.executeToMap(query);
					cbWatchedEpisode.setModel(new DefaultComboBoxModel(mapEpisodes.values().toArray()));
					cbWatchedSubscriber.setModel(new DefaultComboBoxModel(mapSubscriberNames.values().toArray()));
					query = "SELECT Profile.Id, Profile.ProfileName" + 
							" FROM Profile WHERE Profile.SubscriberId = " + getKeyFromValue(mapSubscriberNames, cbWatchedSubscriber.getSelectedItem().toString()) ;
					mapProfiles = dbHandler.executeToMap(query);
					cbWatchedProfile.setModel(new DefaultComboBoxModel(mapProfiles.values().toArray()));
					
					//execute a query which makes deceivering who watched what easier
					tableWatched.setModel(dbHandler.execute("SELECT Watched.id, Episode.Title as Titel, Profile.ProfileName, Watched.PercentWatched\r\n" + 
							"FROM Watched\r\n" + 
							"INNER JOIN Episode ON Watched.ProgramId = Episode.ProgramId\r\n" + 
							"INNER JOIN Profile ON Watched.ProfileId = Profile.Id\r\n" + 
							"UNION ALL\r\n" + 
							"SELECT Watched.id, Movie.Title as Titel, Profile.ProfileName, Watched.PercentWatched\r\n" + 
							"FROM Watched\r\n" + 
							"INNER JOIN Movie ON Watched.ProgramId = Movie.ProgramId\r\n" + 
							"INNER JOIN Profile ON Watched.ProfileId = Profile.Id;\r\n" + 
							""));
					break;
				//1st overview tab clicked, the combobox on this page should have his content filled with the map created holding the series
				case 3:
					cbOverview1Serie.setModel(new DefaultComboBoxModel(mapSerieTitles.values().toArray()));
					break;
				//2nd overview tab clicked, the comboboxes on this page should have his content filled with the maps for subscribers and series
				case 4:
					cbOverview2Serie.setModel(new DefaultComboBoxModel(mapSerieTitles.values().toArray()));
					cbOverview2Subscriber.setModel(new DefaultComboBoxModel(mapSubscriberNames.values().toArray()));
					break;
				//3rd overview tab clicked, the combobox on this page should have his content filled with the map created holding the subscribers
				case 5:
					cbOverview3Subscriber.setModel(new DefaultComboBoxModel(mapSubscriberNames.values().toArray()));
					break;
				//4th overview tab clicked, since there is no user input required we can directly fill the table in this tab with the query
				case 6:
					query = "select top(1) Title as Titel, Duration as Tijdsduur" + 
							" from Movie" + 
							" where MinimalAge < 16";
					tableOverview4.setModel(dbHandler.execute(query));
					break;
				//5th overview tab clicked, since there is no user input required we can directly fill the table in this tab with the query
				case 7:
					 query = "Select  Subscriber.FullName as Naam, count(SubscriberId) as Profielen" + 
							" from Profile" + 
							" INNER JOIN Subscriber ON Subscriber.Id = Profile.SubscriberId" + 
							" group by FullName" + 
							" having count(SubscriberId) = 1";
					 tableOverview5.setModel(dbHandler.execute(query));
					break;
				//6th overview tab clicked, the combobox on this page should have his content filled with the map created holding the movies
				case 8:
					cbOverview6Movie.setModel(new DefaultComboBoxModel(mapMovieTitles.values().toArray()));
					break;
				}
			}
		});
		frmNetflixStatistix.getContentPane().add(tabbedPane);
		
		//---------------------------------------------------------------------------------------------------------------------------------------------
		//Creating the panel for subscribers (called account in code)
		//---------------------------------------------------------------------------------------------------------------------------------------------
		
		//Basic initialization
		JPanel pnlAccount = new JPanel();
		tabbedPane.addTab("Subscriber", null, pnlAccount, null);
		pnlAccount.setLayout(new GridLayout(2, 1, 0, 0));
		
		JPanel pnlAccountForm = new JPanel();
		pnlAccount.add(pnlAccountForm);
		pnlAccountForm.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel PnlAccountFormRw1 = new JPanel();
		pnlAccountForm.add(PnlAccountFormRw1);
		PnlAccountFormRw1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JRadioButton rbAccountAdd = new JRadioButton("Toevoegen");
		//Adding an event to our radio button the see if it has been clicked
		rbAccountAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//If the radiobutton "Add" is selected the program should disable the ID field and enable the rest.
				//Id is not needed when adding a new user as it is created automaticly by the database
				if(rbAccountAdd.isSelected())
				{
					spinAccountID.setEnabled(false);
					tfAccountName.setEnabled(true);
					tfAccountStreet.setEnabled(true);
					tfAccountZip.setEnabled(true);
					tfAccountCity.setEnabled(true);
					tfAccountHouseNr.setEnabled(true);
				}
			}
		});
		rbAccountAdd.setSelected(true);
		//adding the radiobuttons to a group so multiple can't be activated at the same time
		btnGroupAccount.add(rbAccountAdd);
		PnlAccountFormRw1.add(rbAccountAdd);
		
		JRadioButton rbAccountUpdate = new JRadioButton("Updaten");
		//Adding an event to our radio button the see if it has been clicked
		rbAccountUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//If the radiobutton "Update" is selected the program should enable all the fields
				//Id is needed for update to indicate which field you want to change
				if(rbAccountUpdate.isSelected())
				{
					spinAccountID.setEnabled(true);
					tfAccountName.setEnabled(true);
					tfAccountStreet.setEnabled(true);
					tfAccountZip.setEnabled(true);
					tfAccountCity.setEnabled(true);
					tfAccountHouseNr.setEnabled(true);
				}
			}
		});
		//adding the radiobuttons to a group so multiple can't be activated at the same time
		btnGroupAccount.add(rbAccountUpdate);
		PnlAccountFormRw1.add(rbAccountUpdate);
		
		JRadioButton rbAccountDelete = new JRadioButton("Verwijderen");
		rbAccountDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//If the radiobutton "Delete" is selected the program should disable all the fields except for Id
				//Only an Id is required to delete a field in this case
				if(rbAccountDelete.isSelected())
				{
					spinAccountID.setEnabled(true);
					tfAccountName.setEnabled(false);
					tfAccountStreet.setEnabled(false);
					tfAccountZip.setEnabled(false);
					tfAccountCity.setEnabled(false);
					tfAccountHouseNr.setEnabled(false);
				}
			}
		});
		//adding the radiobuttons to a group so multiple can't be activated at the same time
		btnGroupAccount.add(rbAccountDelete);
		PnlAccountFormRw1.add(rbAccountDelete);
		
		JPanel PnlAccountFormRw2 = new JPanel();
		pnlAccountForm.add(PnlAccountFormRw2);
		PnlAccountFormRw2.setLayout(new GridLayout(0, 5, 0, 0));
		
		JLabel lblAccountID = new JLabel("ID");
		lblAccountID.setHorizontalAlignment(SwingConstants.CENTER);
		PnlAccountFormRw2.add(lblAccountID);
		
		spinAccountID = new JSpinner();
		spinAccountID.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		spinAccountID.setEnabled(false);
		PnlAccountFormRw2.add(spinAccountID);
		
		JLabel lblAccountHouseNr = new JLabel("Huisnummer");
		lblAccountHouseNr.setHorizontalAlignment(SwingConstants.CENTER);
		PnlAccountFormRw2.add(lblAccountHouseNr);
		
		tfAccountHouseNr = new JTextField();
		tfAccountHouseNr.setColumns(14);
		//adding a limit to the textfield
		tfAccountHouseNr.setDocument(new JTextFieldLimit(6));
		PnlAccountFormRw2.add(tfAccountHouseNr);
		
		JPanel PnlAccountFormRw3 = new JPanel();
		pnlAccountForm.add(PnlAccountFormRw3);
		PnlAccountFormRw3.setLayout(new GridLayout(0, 5, 0, 0));
		
		JLabel lblAccountName = new JLabel("Naam");
		lblAccountName.setHorizontalAlignment(SwingConstants.CENTER);
		PnlAccountFormRw3.add(lblAccountName);
		
		tfAccountName = new JTextField();
		//adding a limit to the textfield
		tfAccountName.setDocument(new JTextFieldLimit(30));
		PnlAccountFormRw3.add(tfAccountName);
		tfAccountName.setColumns(16);
		
		JLabel lblAccountCity = new JLabel("Stad");
		lblAccountCity.setHorizontalAlignment(SwingConstants.CENTER);
		PnlAccountFormRw3.add(lblAccountCity);
		tfAccountCity = new JTextField();
		//adding a limit to the textfield
		tfAccountCity.setDocument(new JTextFieldLimit(25));
		tfAccountCity.setColumns(10);
		PnlAccountFormRw3.add(tfAccountCity);
		
		JPanel PnlAccountFormRw4 = new JPanel();
		pnlAccountForm.add(PnlAccountFormRw4);
		PnlAccountFormRw4.setLayout(new GridLayout(0, 5, 0, 0));
		
		JLabel lblAccountStreet = new JLabel("Straat");
		lblAccountStreet.setHorizontalAlignment(SwingConstants.CENTER);
		PnlAccountFormRw4.add(lblAccountStreet);
		
		tfAccountStreet = new JTextField();
		//adding a limit to the textfield
		tfAccountStreet.setDocument(new JTextFieldLimit(50));
		PnlAccountFormRw4.add(tfAccountStreet);
		tfAccountStreet.setColumns(8);
		
		JPanel PnlAccountFormRw5 = new JPanel();
		pnlAccountForm.add(PnlAccountFormRw5);
		PnlAccountFormRw5.setLayout(new GridLayout(0, 5, 0, 0));
		
		JLabel lblAccountZip = new JLabel("Postcode");
		lblAccountZip.setHorizontalAlignment(SwingConstants.CENTER);
		PnlAccountFormRw5.add(lblAccountZip);
		
		tfAccountZip = new JTextField();
		//adding a limit to the textfield
		tfAccountZip.setDocument(new JTextFieldLimit(6));
		PnlAccountFormRw5.add(tfAccountZip);
		tfAccountZip.setColumns(4);
		
		JPanel PnlAccountFormRw6 = new JPanel();
		pnlAccountForm.add(PnlAccountFormRw6);
		PnlAccountFormRw6.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnAccountExecute = new JButton("Voer Uit");
		//Button responsible for executing the queries based on radiobutton selected
		//Add a listener to the button
		btnAccountExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//if radiobutton add is selected
				if(rbAccountAdd.isSelected())
				{
					//if the fields are not empty
					if(!tfAccountName.getText().equals("") && !tfAccountStreet.getText().equals("") && !tfAccountZip.getText().equals("") && !tfAccountHouseNr.getText().equals("") && !tfAccountCity.getText().equals(""))
					{
						//call the dbHandler to execute a query with values from the form
						dbHandler.execute("INSERT INTO Subscriber (FullName, Street, ZipCode, HouseNumber, City) VALUES ('"
								+ tfAccountName.getText() + "', '" + tfAccountStreet.getText() + "', '" + tfAccountZip.getText() + "', '" + tfAccountHouseNr.getText() + "', '" + tfAccountCity.getText() + "');");
						//if a new subscriber is added the map of subscribers should be updated
						mapSubscriberNames = dbHandler.executeToMap("SELECT Subscriber.Id, Subscriber.FullName FROM Subscriber");
					}
					else
					{
						WarningMessage.infoBox("Niet alle velden zijn ingevuld", "Error");
					}
				}
				//if radiobutton update is selected
				else if(rbAccountUpdate.isSelected())
				{
					//if the fields are not empty
					if(!tfAccountName.getText().equals("") && !tfAccountStreet.getText().equals("") && !tfAccountZip.getText().equals("") && !tfAccountHouseNr.getText().equals("") && !tfAccountCity.getText().equals(""))
					{
						//call the dbHandler to execute a query with values from the form
						dbHandler.execute("UPDATE Subscriber SET FullName = '" + tfAccountName.getText() + "', Street = '" + tfAccountStreet.getText() + "', ZipCode = '" + tfAccountZip.getText() + 
								"', HouseNumber = '" + tfAccountHouseNr.getText() + "', City = '" + tfAccountCity.getText() + "' WHERE Id = " + spinAccountID.getValue());
						//if a subscriber is updated the map of subscribers should be updated
						mapSubscriberNames = dbHandler.executeToMap("SELECT Subscriber.Id, Subscriber.FullName FROM Subscriber");
					}
					else
					{
						WarningMessage.infoBox("Niet alle velden zijn ingevuld", "Error");
					}
				}
				//if radiobutton delete is selected
				else if(rbAccountDelete.isSelected())
				{
						//call the dbHandler to execute a query with values from the form
						dbHandler.execute("DELETE FROM Subscriber WHERE Id = " + spinAccountID.getValue());
				}
				//after altering the table the gridview should update
				tableAccount.setModel(dbHandler.execute("SELECT * FROM Subscriber"));
			}
		});
		PnlAccountFormRw6.add(btnAccountExecute);
		
		JScrollPane scrollPane = new JScrollPane();
		pnlAccount.add(scrollPane);
		
		tableAccount = new JTable();
		tableAccount.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				//get the the row based on where clicked
				int row = tableAccount.rowAtPoint(evt.getPoint());
				//get the the col based on where clicked
		        int col = tableAccount.columnAtPoint(evt.getPoint());
		      //make sure the row and col start at 0 to prevent weird things
		        if (row >= 0 && col >= 0) {
		        	//set the values based on the row clicked and the column assigned
		        	spinAccountID.setValue(tableAccount.getModel().getValueAt(row, 0));
		        	tfAccountName.setText(tableAccount.getModel().getValueAt(row, 1).toString());
		        	tfAccountStreet.setText(tableAccount.getModel().getValueAt(row, 2).toString());
		        	tfAccountZip.setText(tableAccount.getModel().getValueAt(row, 3).toString());
		        	tfAccountHouseNr.setText(tableAccount.getModel().getValueAt(row, 4).toString());
		        	tfAccountCity.setText(tableAccount.getModel().getValueAt(row, 5).toString());

		        }
			}
		});
		tableAccount.setEnabled(false);
		tableAccount.setFillsViewportHeight(true);
		scrollPane.setViewportView(tableAccount);
		
		//---------------------------------------------------------------------------------------------------------------------------------------------
		//Creating the panel for Profile
		//---------------------------------------------------------------------------------------------------------------------------------------------
		
		JPanel pnlProfile = new JPanel();
		tabbedPane.addTab("Profiel", null, pnlProfile, null);
		pnlProfile.setLayout(new GridLayout(2, 1, 0, 0));
		
		JPanel pnlProfileForm = new JPanel();
		pnlProfile.add(pnlProfileForm);
		pnlProfileForm.setLayout(new GridLayout(6, 6, 0, 0));
		
		JPanel pnlProfileFormRw1 = new JPanel();
		pnlProfileForm.add(pnlProfileFormRw1);
		pnlProfileFormRw1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JRadioButton rbProfileAdd = new JRadioButton("Toevoegen");
		rbProfileAdd.setSelected(true);
		//Adding an event to our radio button the see if it has been clicked
		rbProfileAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//If the radiobutton "Add" is selected the program should disable the ID field and enable the rest.
				//Id is not needed when adding a new user as it is created automaticly by the database
				if(rbProfileAdd.isSelected())
				{
					spinProfileID.setEnabled(false);
					tfProfileName.setEnabled(true);
					spinProfileSubscriberID.setEnabled(true);
					spinProfileDay.setEnabled(true);
					spinProfileMonth.setEnabled(true);
					spinProfileYear.setEnabled(true);
				}
			}
		});
		//adding the radiobuttons to a group so multiple can't be activated at the same time
		btnGroupProfile.add(rbProfileAdd);
		pnlProfileFormRw1.add(rbProfileAdd);
		
		JRadioButton rbProfileUpdate = new JRadioButton("Updaten");
		//Adding an event to our radio button the see if it has been clicked
		rbProfileUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				//If the radiobutton "Update" is selected the program should enable all the fields
				//Id is needed for update to indicate which field you want to change
				if(rbProfileUpdate.isSelected()){
					spinProfileID.setEnabled(true);
					tfProfileName.setEnabled(true);
					spinProfileSubscriberID.setEnabled(true);
					spinProfileDay.setEnabled(true);
					spinProfileMonth.setEnabled(true);
					spinProfileYear.setEnabled(true);
				}
			}
		});
		//adding the radiobuttons to a group so multiple can't be activated at the same time
		btnGroupProfile.add(rbProfileUpdate);
		pnlProfileFormRw1.add(rbProfileUpdate);
		
		JRadioButton rbProfileDelete = new JRadioButton("Verwijderen");
		//Adding an event to our radio button the see if it has been clicked
		rbProfileDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//If the radiobutton "Delete" is selected the program should disable all the fields except for Id
				//Only an Id is required to delete a field in this case
				if(rbProfileDelete.isSelected())
				{
					spinProfileID.setEnabled(true);
					tfProfileName.setEnabled(false);
					spinProfileSubscriberID.setEnabled(false);
					spinProfileDay.setEnabled(false);
					spinProfileMonth.setEnabled(false);
					spinProfileYear.setEnabled(false);
				}
			}
		});
		//adding the radiobuttons to a group so multiple can't be activated at the same time
		btnGroupProfile.add(rbProfileDelete);
		pnlProfileFormRw1.add(rbProfileDelete);
		
		JPanel pnlProfileFormRw2 = new JPanel();
		pnlProfileForm.add(pnlProfileFormRw2);
		pnlProfileFormRw2.setLayout(new GridLayout(0, 4, 0, 0));
		
		JLabel lblProfileID = new JLabel("ID");
		lblProfileID.setHorizontalAlignment(SwingConstants.CENTER);
		pnlProfileFormRw2.add(lblProfileID);
		
		spinProfileID = new JSpinner();
		spinProfileID.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		spinProfileID.setEnabled(false);
		pnlProfileFormRw2.add(spinProfileID);

		
		JLabel lblProfileSubscriberID = new JLabel("Subscriber ID");
		lblProfileSubscriberID.setHorizontalAlignment(SwingConstants.CENTER);
		pnlProfileFormRw2.add(lblProfileSubscriberID);
		
		spinProfileSubscriberID = new JSpinner();
		spinProfileSubscriberID.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		pnlProfileFormRw2.add(spinProfileSubscriberID);
		
		JPanel pnlProfileFormRw3 = new JPanel();
		pnlProfileForm.add(pnlProfileFormRw3);
		pnlProfileFormRw3.setLayout(new GridLayout(0, 4, 0, 0));
		
		JLabel lblProfileName = new JLabel("Profiel Naam");
		lblProfileName.setHorizontalAlignment(SwingConstants.CENTER);
		pnlProfileFormRw3.add(lblProfileName);
		
		tfProfileName = new JTextField();
		//limit characters of profilename
		tfProfileName.setDocument(new JTextFieldLimit(20));
		tfProfileName.setColumns(16);
		pnlProfileFormRw3.add(tfProfileName);
		
		JPanel pnlProfileFormRw4 = new JPanel();
		pnlProfileForm.add(pnlProfileFormRw4);
		pnlProfileFormRw4.setLayout(new GridLayout(0, 4, 0, 0));
		
		JLabel lblProfileDOB = new JLabel("Geb. Datum(dd.mm.yyyy)");
		lblProfileDOB.setHorizontalAlignment(SwingConstants.CENTER);
		pnlProfileFormRw4.add(lblProfileDOB);
		
		JPanel pnlProfileDobPicker = new JPanel();
		pnlProfileFormRw4.add(pnlProfileDobPicker);
		pnlProfileDobPicker.setLayout(new GridLayout(0, 3, 0, 0));
		
		spinProfileDay = new JSpinner();
		spinProfileDay.setModel(new SpinnerNumberModel(1, 1, 31, 1));
		pnlProfileDobPicker.add(spinProfileDay);
		
		spinProfileMonth = new JSpinner();
		spinProfileMonth.setModel(new SpinnerNumberModel(1, 1, 12, 1));
		pnlProfileDobPicker.add(spinProfileMonth);
		
		spinProfileYear = new JSpinner();
		spinProfileYear.setModel(new SpinnerNumberModel(1990, 1900, 2018, 1));
		pnlProfileDobPicker.add(spinProfileYear);
		
		JPanel pnlProfileFormRw5 = new JPanel();
		pnlProfileForm.add(pnlProfileFormRw5);
		pnlProfileFormRw5.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel pnlProfileFormRw6 = new JPanel();
		pnlProfileForm.add(pnlProfileFormRw6);
		pnlProfileFormRw6.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		//Button responsible for executing the queries based on radiobutton selected
		//Add a listener to the button
		JButton btnProfileExecute = new JButton("Voer Uit");
		pnlProfileFormRw6.add(btnProfileExecute);
		btnProfileExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//if radiobutton add is selected
				if(rbProfileAdd.isSelected())
				{
					//if the fields are not empty
					if(!tfProfileName.getText().equals(""))
					{
						//call the dbHandler to execute a query with values from the form
						dbHandler.execute("INSERT INTO Profile (SubscriberId, ProfileName, DOB) VALUES ('" + spinProfileSubscriberID.getValue() + "', '" + tfProfileName.getText() + "','" + 
						spinProfileYear.getValue() + "-" + spinProfileMonth.getValue()+ "-" + spinProfileDay.getValue() + "');");
					}
					else
					{
						WarningMessage.infoBox("Niet alle velden zijn ingevuld", "Error");
					}
				}
				//if radiobutton update is selected
				else if(rbProfileUpdate.isSelected())
				{
					//if the fields are not empty
					if(!tfProfileName.getText().equals(""))
					{
						//call the dbHandler to execute a query with values from the form
						dbHandler.execute("UPDATE Profile SET SubscriberId = '" + spinProfileSubscriberID.getValue() + "', ProfileName = '" + tfProfileName.getText() + "', DOB = '" + 
								spinProfileYear.getValue() + "-" + spinProfileMonth.getValue()+ "-" + spinProfileDay.getValue() + "' WHERE Id = " + spinProfileID.getValue());
					}
					else
					{
						WarningMessage.infoBox("Niet alle velden zijn ingevuld", "Error");
					}
				}
				//if radiobutton delete is selected
				else if(rbProfileDelete.isSelected())
				{
					//if the fields are not empty
					if(!spinProfileID.equals(""))
					{
						dbHandler.execute("DELETE FROM Profile WHERE Id = " + spinProfileID.getValue());
					}
				}
				//after altering the table the gridview should update
				tableProfile.setModel(dbHandler.execute("SELECT * FROM Profile"));
			}
		});
		
		
		JScrollPane scrollPnlProfileGrid = new JScrollPane();
		pnlProfile.add(scrollPnlProfileGrid);
		tableProfile = new JTable();
		//added listener to table when a row is clicked the data of row will be automaticly added in the form.
		tableProfile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				//get the the row based on where clicked
				int row = tableProfile.rowAtPoint(evt.getPoint());
				//get the the col based on where clicked
		        int col = tableProfile.columnAtPoint(evt.getPoint());
		      //make sure the row and col start at 0 to prevent weird things
		        if (row >= 0 && col >= 0) {
		        	//set the values based on the row clicked and the column assigned
		        	spinProfileID.setValue(tableProfile.getModel().getValueAt(row, 0));
		        	tfProfileName.setText(tableProfile.getModel().getValueAt(row, 1).toString());
		        	spinProfileSubscriberID.setValue(tableProfile.getModel().getValueAt(row, 2));
		        	//It works but Java dates are really weird and somehow after an hour trying this worked
		        	//It just gets the Date from the table set it to date then it gets the full date string and it we split that string to get the full year
		        	//instead of a only the last 2 numbers. We do this by splitting the date string and converting it to an int
		        	//month works but was behind one month.
		        	//Surrounded by try catch because of all the converting and potential instability
		        	try {
						Date date = new SimpleDateFormat("yyyy-MM-dd").parse(tableProfile.getValueAt(row, 3).toString());				
						spinProfileYear.setValue(Integer.parseInt(date.toGMTString().split(" ")[2]));
						spinProfileMonth.setValue(date.getMonth() + 1);
						spinProfileDay.setValue(Integer.parseInt(date.toGMTString().split(" ")[0]) + 1);
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						WarningMessage.infoBox("Er is iets mis gegaan met het converten van de datum", "Error");
						e.printStackTrace();
					}
		        }
			}
		});
		tableProfile.setColumnSelectionAllowed(true);
		tableProfile.setCellSelectionEnabled(true);
		tableProfile.setEnabled(false);
		tableProfile.setFillsViewportHeight(true);
		scrollPnlProfileGrid.setViewportView(tableProfile);
		
		//---------------------------------------------------------------------------------------------------------------------------------------------
		//Creating the panel for Watched
		//---------------------------------------------------------------------------------------------------------------------------------------------
		
		JPanel pnlWatched = new JPanel();
		tabbedPane.addTab("Bekeken", null, pnlWatched, null);
		pnlWatched.setLayout(new GridLayout(2, 1, 0, 0));
		
		JPanel pnlWatchedForm = new JPanel();
		pnlWatched.add(pnlWatchedForm);
		pnlWatchedForm.setLayout(new GridLayout(6, 0, 0, 0));
		
		JPanel pnlWatchedFormRw1 = new JPanel();
		pnlWatchedForm.add(pnlWatchedFormRw1);
		pnlWatchedFormRw1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JRadioButton rbWatchedAdd = new JRadioButton("Toevoegen");
		//Adding an event to our radio button the see if it has been clicked
		rbWatchedAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//If the radiobutton "Add" is selected the program should disable the ID field and enable the rest.
				//Id is not needed when adding a new user as it is created automaticly by the database
				if(rbWatchedAdd.isSelected())
				{
					spinWatchedID.setEnabled(false);
					spinWatchedPercentage.setEnabled(true);
					cbWatchedProgram.setEnabled(true);
					cbWatchedType.setEnabled(true);
					cbWatchedSubscriber.setEnabled(true);
					cbWatchedProfile.setEnabled(true);
					boolean sIndex = (cbWatchedType.getSelectedIndex() == 0) ? true : false;
					cbWatchedEpisode.setEnabled(sIndex);
				}
			}
		});
		
		rbWatchedAdd.setSelected(true);
		btnGroupWatched.add(rbWatchedAdd);
		pnlWatchedFormRw1.add(rbWatchedAdd);
		
		JRadioButton rbWatchedUpdate = new JRadioButton("Updaten");
		//Adding an event to our radio button the see if it has been clicked
		rbWatchedUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//If the radiobutton "Update" is selected the program should enable all the fields
				//Id is needed for update to indicate which field you want to change
				if(rbWatchedUpdate.isSelected())
				{
					spinWatchedID.setEnabled(true);
					spinWatchedPercentage.setEnabled(true);
					cbWatchedProgram.setEnabled(true);
					cbWatchedType.setEnabled(true);
					cbWatchedSubscriber.setEnabled(true);
					cbWatchedProfile.setEnabled(true);
					boolean sIndex = (cbWatchedType.getSelectedIndex() == 0) ? true : false;
					cbWatchedEpisode.setEnabled(sIndex);
				}
			}
		});
		btnGroupWatched.add(rbWatchedUpdate);
		pnlWatchedFormRw1.add(rbWatchedUpdate);
		
		JRadioButton rbWatchedDelete = new JRadioButton("Verwijderen");
		rbWatchedDelete.addActionListener(new ActionListener() {
		//Adding an event to our radio button the see if it has been clicked
			public void actionPerformed(ActionEvent e) {
				//If the radiobutton "Delete" is selected the program should disable all the fields except for Id
				//Only an Id is required to delete a field in this case
				if(rbWatchedDelete.isSelected())
				{
					spinWatchedID.setEnabled(true);
					spinWatchedPercentage.setEnabled(false);
					cbWatchedProgram.setEnabled(false);
					cbWatchedType.setEnabled(false);
					cbWatchedSubscriber.setEnabled(false);
					cbWatchedProfile.setEnabled(false);
					cbWatchedEpisode.setEnabled(false);
				}
			}
		});
		btnGroupWatched.add(rbWatchedDelete);
		pnlWatchedFormRw1.add(rbWatchedDelete);
		
		JPanel pnlWatchedFormRw2 = new JPanel();
		pnlWatchedForm.add(pnlWatchedFormRw2);
		pnlWatchedFormRw2.setLayout(new GridLayout(0, 4, 0, 0));
		
		JLabel lblWatchedID = new JLabel("ID");
		lblWatchedID.setHorizontalAlignment(SwingConstants.CENTER);
		pnlWatchedFormRw2.add(lblWatchedID);
		
		spinWatchedID = new JSpinner();
		spinWatchedID.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		spinWatchedID.setEnabled(false);
		pnlWatchedFormRw2.add(spinWatchedID);
		
		JLabel lblWatchedSubscriber = new JLabel("Selecteer Subscriber");
		lblWatchedSubscriber.setHorizontalAlignment(SwingConstants.CENTER);
		pnlWatchedFormRw2.add(lblWatchedSubscriber);
		
		cbWatchedSubscriber = new JComboBox();
		cbWatchedSubscriber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String query = "SELECT Profile.Id, Profile.ProfileName" + 
						" FROM Profile WHERE Profile.SubscriberId = " + getKeyFromValue(mapSubscriberNames, cbWatchedSubscriber.getSelectedItem().toString()) ;
				mapProfiles = dbHandler.executeToMap(query);
				cbWatchedProfile.setModel(new DefaultComboBoxModel(mapProfiles.values().toArray()));
			}
		});
		pnlWatchedFormRw2.add(cbWatchedSubscriber);
		
		JPanel pnlWatchedFormRw3 = new JPanel();
		pnlWatchedForm.add(pnlWatchedFormRw3);
		pnlWatchedFormRw3.setLayout(new GridLayout(0, 4, 0, 0));
		
		JLabel lblWatchedType = new JLabel("Selecteer Type");
		lblWatchedType.setHorizontalAlignment(SwingConstants.CENTER);
		pnlWatchedFormRw3.add(lblWatchedType);
		
		cbWatchedType = new JComboBox();
		cbWatchedType.setModel(new DefaultComboBoxModel(new String[] {"Aflevering", "Film"}));
		cbWatchedType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(cbWatchedType.getSelectedIndex() == 0)
				{
					cbWatchedProgram.setModel(new DefaultComboBoxModel(mapSerieTitles.values().toArray()));
					cbWatchedEpisode.setEnabled(true);
				}
				else
				{
					cbWatchedProgram.setModel(new DefaultComboBoxModel(mapMovieTitles.values().toArray()));
					cbWatchedEpisode.setEnabled(false);
				}
			}
		});
		pnlWatchedFormRw3.add(cbWatchedType);
		
		JLabel lblWatchedProfile = new JLabel("Selecteer Profiel");
		pnlWatchedFormRw3.add(lblWatchedProfile);
		lblWatchedProfile.setHorizontalAlignment(SwingConstants.CENTER);
		
		cbWatchedProfile = new JComboBox();
		pnlWatchedFormRw3.add(cbWatchedProfile);
		
		JPanel pnlWatchedFormRw4 = new JPanel();
		pnlWatchedForm.add(pnlWatchedFormRw4);
		pnlWatchedFormRw4.setLayout(new GridLayout(0, 4, 0, 0));
		
		JLabel lblWatchedProgram = new JLabel("Selecteer ...");
		lblWatchedProgram.setHorizontalAlignment(SwingConstants.CENTER);
		pnlWatchedFormRw4.add(lblWatchedProgram);
		
		cbWatchedProgram = new JComboBox();
		cbWatchedProgram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(cbWatchedType.getSelectedIndex() == 0) 
				{
					String query = "SELECT Episode.Id, Episode.Title" + 
							" FROM Episode WHERE SerieId = " + getKeyFromValue(mapSerieTitles, cbWatchedProgram.getSelectedItem().toString()) ;
					mapEpisodes = dbHandler.executeToMap(query);
					cbWatchedEpisode.setModel(new DefaultComboBoxModel(mapEpisodes.values().toArray()));
				}
			}
		});
		pnlWatchedFormRw4.add(cbWatchedProgram);
		
		JLabel lblWatchedPercentage = new JLabel("Percentage bekeken");
		lblWatchedPercentage.setHorizontalAlignment(SwingConstants.CENTER);
		pnlWatchedFormRw4.add(lblWatchedPercentage);
		
		spinWatchedPercentage = new JSpinner();
		pnlWatchedFormRw4.add(spinWatchedPercentage);
		spinWatchedPercentage.setModel(new SpinnerNumberModel(0, null, 100, 1));
		
		JPanel pnlWatchedFormRw5 = new JPanel();
		pnlWatchedForm.add(pnlWatchedFormRw5);
		pnlWatchedFormRw5.setLayout(new GridLayout(0, 4, 0, 0));
		
		JLabel lblWatchedEpisode = new JLabel("Selecteer Aflevering");
		lblWatchedEpisode.setHorizontalAlignment(SwingConstants.CENTER);
		pnlWatchedFormRw5.add(lblWatchedEpisode);
		
		cbWatchedEpisode = new JComboBox();
		pnlWatchedFormRw5.add(cbWatchedEpisode);
		
		JPanel pnlWatchedFormRw6 = new JPanel();
		pnlWatchedForm.add(pnlWatchedFormRw6);
		pnlWatchedFormRw6.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		//Button responsible for executing the queries based on radiobutton selected
		//Add a listener to the button
		JButton btnWatchedExecute = new JButton("Voer Uit");
		pnlWatchedFormRw6.add(btnWatchedExecute);
		btnWatchedExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				//0 means it is an episode
				int programId = 0;
				int profileId = 0;
				if (cbWatchedProfile.getSelectedIndex() != -1)
				{
					profileId = (int) getKeyFromValue(mapProfiles, cbWatchedProfile.getSelectedItem().toString());
				}
				if (cbWatchedType.getSelectedIndex() == 0)
				{
					programId = (int) getKeyFromValue(mapSerieTitles, cbWatchedProgram.getSelectedItem().toString());
				}
				else
				{
					programId = (int) getKeyFromValue(mapMovieTitles, cbWatchedProgram.getSelectedItem().toString());
				}
				
				//if radiobutton add is selected
				if(rbWatchedAdd.isSelected())
				{	
					if((cbWatchedEpisode.getSelectedIndex() == -1 && cbWatchedType.getSelectedIndex() == 0) || (cbWatchedProgram.getSelectedIndex() == -1 || 
							cbWatchedSubscriber.getSelectedIndex() == -1 || cbWatchedProfile.getSelectedIndex() == -1))
					{
						WarningMessage.infoBox("Niet alle velden zijn ingevuld", "Error");
					}
					else
					{
						//call the dbHandler to execute a query with values from the form
						dbHandler.execute("INSERT INTO Watched (ProgramId, ProfileId, PercentWatched) VALUES ('" + programId + "', '" + profileId + 
								"', '" + spinWatchedPercentage.getValue() + "');");
					}
				}
				//if radiobutton update is selected
				else if(rbWatchedUpdate.isSelected())
				{
					if((cbWatchedEpisode.getSelectedIndex() == -1 && cbWatchedType.getSelectedIndex() == 0) || (cbWatchedProgram.getSelectedIndex() == -1 || 
							cbWatchedSubscriber.getSelectedIndex() == -1 || cbWatchedProfile.getSelectedIndex() == -1))
					{
						WarningMessage.infoBox("Niet alle velden zijn ingevuld", "Error");
					}
					else
					{

					//call the dbHandler to execute a query with values from the form
					dbHandler.execute("UPDATE Watched SET ProgramId = '" + programId + "', ProfileId = '" + profileId + "', PercentWatched = '" + 
							spinWatchedPercentage.getValue() + "' WHERE Id = " + spinWatchedID.getValue());
					}
				}
				//if radiobutton delete is selected
				else if(rbWatchedDelete.isSelected())
				{
					//call the dbHandler to execute a query with values from the form
					dbHandler.execute("DELETE FROM Watched WHERE Id = " + spinWatchedID.getValue());
				}
				tableWatched.setModel(dbHandler.execute("SELECT Watched.id, Episode.Title as Titel, Profile.ProfileName, Watched.PercentWatched\r\n" + 
						"FROM Watched\r\n" + 
						"INNER JOIN Episode ON Watched.ProgramId = Episode.ProgramId\r\n" + 
						"INNER JOIN Profile ON Watched.ProfileId = Profile.Id\r\n" + 
						"UNION ALL\r\n" + 
						"SELECT Watched.id, Movie.Title as Titel, Profile.ProfileName, Watched.PercentWatched\r\n" + 
						"FROM Watched\r\n" + 
						"INNER JOIN Movie ON Watched.ProgramId = Movie.ProgramId\r\n" + 
						"INNER JOIN Profile ON Watched.ProfileId = Profile.Id;\r\n" + 
						""));
			}
		});
		
		JScrollPane scrollPnlWatchedGrid = new JScrollPane();
		pnlWatched.add(scrollPnlWatchedGrid);
		
		tableWatched = new JTable();
		tableWatched.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				//get the the row based on where clicked
				int row = tableWatched.rowAtPoint(evt.getPoint());
				//get the the col based on where clicked
		        int col = tableWatched.columnAtPoint(evt.getPoint());
		      //make sure the row and col start at 0 to prevent weird things
		        if (row >= 0 && col >= 0) {
		        	//set the values based on the row clicked and the column assigned
		        	spinWatchedID.setValue(tableWatched.getModel().getValueAt(row, 0));
					int percent = Integer.parseInt(tableWatched.getModel().getValueAt(row, 3).toString());
		        	spinWatchedPercentage.setValue(percent);
		        }
			}
		});
		tableWatched.setEnabled(false);
		tableWatched.setFillsViewportHeight(true);
		scrollPnlWatchedGrid.setViewportView(tableWatched);
		
		//---------------------------------------------------------------------------------------------------------------------------------------------
		//Creating the panel for Overview1
		//---------------------------------------------------------------------------------------------------------------------------------------------
		
		JPanel pnlOverview1 = new JPanel();
		tabbedPane.addTab("Overzicht 1", null, pnlOverview1, null);
		pnlOverview1.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel pnlOverview1Form = new JPanel();
		pnlOverview1.add(pnlOverview1Form);
		pnlOverview1Form.setLayout(new GridLayout(6, 6, 0, 0));
		
		JPanel pnlOverview1Row2 = new JPanel();
		pnlOverview1Form.add(pnlOverview1Row2);
		pnlOverview1Row2.setLayout(new GridLayout(0, 6, 0, 0));
		
		JPanel pnlOverview1Row3 = new JPanel();
		pnlOverview1Form.add(pnlOverview1Row3);
		pnlOverview1Row3.setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel pnlOverview1Filler1 = new JPanel();
		pnlOverview1Row3.add(pnlOverview1Filler1);
		
		JLabel lblOverview1Serie = new JLabel("Selecteer een serie");
		pnlOverview1Row3.add(lblOverview1Serie);
		lblOverview1Serie.setHorizontalAlignment(SwingConstants.CENTER);
		
		cbOverview1Serie = new JComboBox();
		pnlOverview1Row3.add(cbOverview1Serie);
		
		JPanel pnlOverview1Row4 = new JPanel();
		pnlOverview1Form.add(pnlOverview1Row4);
		pnlOverview1Row4.setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel pnlOverview1Filler2 = new JPanel();
		pnlOverview1Row4.add(pnlOverview1Filler2);
		
		JPanel pnlOverview1Filler3 = new JPanel();
		pnlOverview1Row4.add(pnlOverview1Filler3);
		
		//user selects their show from the combobox thats has been filled when they clicked on the tab
		//button event triggers when user clicks the button.
		//the show that is selected at the moment of clicking will be used in the query.
		//the response of the query will be send to the table
		JButton btnOverview1Results = new JButton("Bekijk");
		pnlOverview1Row4.add(btnOverview1Results);
		btnOverview1Results.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String query = "SELECT Episode.Id, AVG(Watched.PercentWatched) as 'Gemiddeld Bekeken in %', Episode.Title as 'Aflevering Titel'" + 
				" From Watched" + 
				" INNER JOIN Program ON Watched.ProgramId = Program.Id" + 
				" INNER JOIN Episode ON Episode.ProgramId = Program.Id" + 
				" Where Episode.SerieId = " + getKeyFromValue(mapSerieTitles, cbOverview1Serie.getSelectedItem().toString()) +
				" Group by Episode.Title, Episode.Id;";
				tableOverview1.setModel(dbHandler.execute(query));
			}
		});
		
		JPanel pnlOverview1Row5 = new JPanel();
		pnlOverview1Form.add(pnlOverview1Row5);
		pnlOverview1Row5.setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel pnlOverview1Row6 = new JPanel();
		pnlOverview1Form.add(pnlOverview1Row6);
		pnlOverview1Row6.setLayout(new GridLayout(0, 6, 0, 0));
		
		JPanel pnlOverview1Row7 = new JPanel();
		pnlOverview1Form.add(pnlOverview1Row7);
		pnlOverview1Row7.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblOverview1Description = new JLabel("Gemiddeld % bekeken per aflevering");
		lblOverview1Description.setHorizontalAlignment(SwingConstants.CENTER);
		pnlOverview1Row7.add(lblOverview1Description);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		pnlOverview1.add(scrollPane_1);
		
		tableOverview1 = new JTable();
		tableOverview1.setEnabled(false);
		tableOverview1.setFillsViewportHeight(true);
		scrollPane_1.setViewportView(tableOverview1);
		
		//---------------------------------------------------------------------------------------------------------------------------------------------
		//Creating the panel for Overview2
		//---------------------------------------------------------------------------------------------------------------------------------------------
		
		JPanel pnlOverview2 = new JPanel();
		tabbedPane.addTab("Overzicht 2", null, pnlOverview2, null);
		pnlOverview2.setLayout(new GridLayout(2, 0, 0, 0));
		
		JPanel pnlOverview2Form = new JPanel();
		pnlOverview2.add(pnlOverview2Form);
		pnlOverview2Form.setLayout(new GridLayout(6, 6, 0, 0));
		
		JPanel pnlOverview2FormRw1 = new JPanel();
		pnlOverview2Form.add(pnlOverview2FormRw1);
		pnlOverview2FormRw1.setLayout(new GridLayout(0, 6, 0, 0));
		
		JPanel pnlOverview2FormRw3 = new JPanel();
		pnlOverview2Form.add(pnlOverview2FormRw3);
		pnlOverview2FormRw3.setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel pnlOverview2Filler1 = new JPanel();
		pnlOverview2FormRw3.add(pnlOverview2Filler1);
		
		JLabel lblOverview2Subscriber = new JLabel("Selecteer een Subscriber");
		lblOverview2Subscriber.setHorizontalAlignment(SwingConstants.CENTER);
		pnlOverview2FormRw3.add(lblOverview2Subscriber);
		
		cbOverview2Subscriber = new JComboBox();
		pnlOverview2FormRw3.add(cbOverview2Subscriber);
		
		JPanel pnlOverview2FormRw4 = new JPanel();
		pnlOverview2Form.add(pnlOverview2FormRw4);
		pnlOverview2FormRw4.setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel pnlOverview2Filler2 = new JPanel();
		pnlOverview2FormRw4.add(pnlOverview2Filler2);
		
		JLabel lblOverview2Serie = new JLabel("Selecteer een serie");
		lblOverview2Serie.setHorizontalAlignment(SwingConstants.CENTER);
		pnlOverview2FormRw4.add(lblOverview2Serie);
		
		cbOverview2Serie = new JComboBox();
		pnlOverview2FormRw4.add(cbOverview2Serie);
		
		JPanel pnlOverview2FormRw5 = new JPanel();
		pnlOverview2Form.add(pnlOverview2FormRw5);
		pnlOverview2FormRw5.setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel pnlOverview2Filler3 = new JPanel();
		pnlOverview2FormRw5.add(pnlOverview2Filler3);
		
		JPanel pnlOverview2Filler4 = new JPanel();
		pnlOverview2FormRw5.add(pnlOverview2Filler4);
		
		//user selects their show and subscriber from the comboboxes thats has been filled when they clicked on the tab
		//button event triggers when user clicks the button.
		//the show and subscriber that is selected at the moment of clicking will be used in the query.
		//the response of the query will be send to the table
		JButton btnOverview2Results = new JButton("Bekijk");
		btnOverview2Results.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int serieId = (int) getKeyFromValue(mapSerieTitles, cbOverview2Serie.getSelectedItem().toString());
				int SubscriberId = (int) getKeyFromValue(mapSubscriberNames, cbOverview2Subscriber.getSelectedItem().toString());
				String query = "SELECT Episode.Id, Episode.Title as 'Aflevering Titel', AVG(Watched.PercentWatched) as 'Gemiddeld Bekeken in %'" + 
						" From Watched" + 
						" INNER JOIN Program ON Watched.ProgramId = Program.Id" + 
						" INNER JOIN Episode ON Episode.ProgramId = Program.Id" + 
						" INNER JOIN Profile ON Profile.Id = Watched.ProfileId" + 
						" INNER JOIN Subscriber ON Subscriber.Id = Profile.SubscriberId" + 
						" Where Episode.SerieId = " + serieId + " AND Subscriber.Id = " + SubscriberId + 
						" Group by Episode.Id, Episode.Title;";
						tableOverview2.setModel(dbHandler.execute(query));
			}
		});
		pnlOverview2FormRw5.add(btnOverview2Results);
		
		JPanel pnlOverview2FormRw6 = new JPanel();
		pnlOverview2Form.add(pnlOverview2FormRw6);
		pnlOverview2FormRw6.setLayout(new GridLayout(0, 6, 0, 0));
		
		JPanel pnlOverview2FormRw7 = new JPanel();
		pnlOverview2Form.add(pnlOverview2FormRw7);
		pnlOverview2FormRw7.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblOverview2Description = new JLabel("Gemiddeld % bekeken per aflevering van subscriber");
		lblOverview2Description.setHorizontalAlignment(SwingConstants.CENTER);
		pnlOverview2FormRw7.add(lblOverview2Description);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		pnlOverview2.add(scrollPane_2);
		
		tableOverview2 = new JTable();
		tableOverview2.setEnabled(false);
		tableOverview2.setFillsViewportHeight(true);
		scrollPane_2.setViewportView(tableOverview2);
		
		//---------------------------------------------------------------------------------------------------------------------------------------------
		//Creating the panel for Overview3
		//---------------------------------------------------------------------------------------------------------------------------------------------
		
		JPanel pnlOverview3 = new JPanel();
		tabbedPane.addTab("Overzicht 3", null, pnlOverview3, null);
		pnlOverview3.setLayout(new GridLayout(2, 1, 0, 0));
		
		JPanel pnlOverview3Form = new JPanel();
		pnlOverview3.add(pnlOverview3Form);
		pnlOverview3Form.setLayout(new GridLayout(6, 6, 0, 0));
		
		JPanel pnlOverview3FormRw2 = new JPanel();
		pnlOverview3Form.add(pnlOverview3FormRw2);
		pnlOverview3FormRw2.setLayout(new GridLayout(0, 6, 0, 0));
		
		JPanel pnlOverview3FormRw3 = new JPanel();
		pnlOverview3Form.add(pnlOverview3FormRw3);
		pnlOverview3FormRw3.setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel pnlOverview3Filler3 = new JPanel();
		pnlOverview3FormRw3.add(pnlOverview3Filler3);
		
		JLabel lblOverview3Subscriber = new JLabel("Selecteer een Subscriber");
		lblOverview3Subscriber.setHorizontalAlignment(SwingConstants.CENTER);
		pnlOverview3FormRw3.add(lblOverview3Subscriber);
		
		cbOverview3Subscriber = new JComboBox();
		pnlOverview3FormRw3.add(cbOverview3Subscriber);
		
		JPanel pnlOverview3FormRw4 = new JPanel();
		pnlOverview3Form.add(pnlOverview3FormRw4);
		pnlOverview3FormRw4.setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel pnlOverview3Filler1 = new JPanel();
		pnlOverview3FormRw4.add(pnlOverview3Filler1);
		
		JPanel pnlOverview3Filler2 = new JPanel();
		pnlOverview3FormRw4.add(pnlOverview3Filler2);
		
		//user selects their subscriber from the comboboxes
		//button event triggers when user clicks the button.
		//the subscriber that is selected at the moment of clicking will be used in the query.
		//the response of the query will be send to the table
		JButton btnOverview3Results = new JButton("Bekijk");
		btnOverview3Results.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int SubscriberId = (int) getKeyFromValue(mapSubscriberNames, cbOverview3Subscriber.getSelectedItem().toString());
				String query = "SELECT Profile.ProfileName, Movie.Title as 'Aflevering Titel'" + 
						" From Watched" + 
						" INNER JOIN Program ON Watched.ProgramId = Program.Id" + 
						" INNER JOIN Movie ON Movie.ProgramId = Program.Id" + 
						" INNER JOIN Profile ON Profile.Id = Watched.ProfileId" + 
						" INNER JOIN Subscriber ON Subscriber.Id = Profile.SubscriberId" + 
						" Where Subscriber.Id = " + SubscriberId;
						tableOverview3.setModel(dbHandler.execute(query));
			}
		});
		pnlOverview3FormRw4.add(btnOverview3Results);
		
		JPanel pnlOverview3FormRw5 = new JPanel();
		pnlOverview3Form.add(pnlOverview3FormRw5);
		pnlOverview3FormRw5.setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel pnlOverview3FormRw6 = new JPanel();
		pnlOverview3Form.add(pnlOverview3FormRw6);
		pnlOverview3FormRw6.setLayout(new GridLayout(0, 6, 0, 0));
		
		JPanel pnlOverview3FormRw7 = new JPanel();
		pnlOverview3Form.add(pnlOverview3FormRw7);
		pnlOverview3FormRw7.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblOverview3Description = new JLabel("Films bekeken door subscriber");
		lblOverview3Description.setHorizontalAlignment(SwingConstants.CENTER);
		pnlOverview3FormRw7.add(lblOverview3Description);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		pnlOverview3.add(scrollPane_3);
		
		tableOverview3 = new JTable();
		tableOverview3.setFillsViewportHeight(true);
		tableOverview3.setEnabled(false);
		scrollPane_3.setViewportView(tableOverview3);
		
		//---------------------------------------------------------------------------------------------------------------------------------------------
		//Creating the panel for Overview4
		//the logic for this overview has been executed by the tab switch event.
		//---------------------------------------------------------------------------------------------------------------------------------------------
		
		JPanel pnlOverview4 = new JPanel();
		tabbedPane.addTab("Overzicht 4", null, pnlOverview4, null);
		pnlOverview4.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlOverview4Description = new JPanel();
		pnlOverview4.add(pnlOverview4Description, BorderLayout.NORTH);
		
		JLabel lblDeFilmMet = new JLabel("De film met de langste tijdsduur voor kijkers onder de 16 jaar");
		pnlOverview4Description.add(lblDeFilmMet);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		pnlOverview4.add(scrollPane_4, BorderLayout.CENTER);
		
		tableOverview4 = new JTable();
		tableOverview4.setEnabled(false);
		tableOverview4.setFillsViewportHeight(true);
		scrollPane_4.setViewportView(tableOverview4);
		
		//---------------------------------------------------------------------------------------------------------------------------------------------
		//Creating the panel for Overview5
		//the logic for this overview has been executed by the tab switch event.
		//---------------------------------------------------------------------------------------------------------------------------------------------
		
		JPanel pnlOverview5 = new JPanel();
		tabbedPane.addTab("Overzicht 5", null, pnlOverview5, null);
		pnlOverview5.setLayout(new BorderLayout(0, 0));
		

		pnlOverview5.setLayout(new BorderLayout(0, 0));
		JPanel pnlOverview5Description = new JPanel();
		pnlOverview5.add(pnlOverview5Description, BorderLayout.NORTH);
		
		JLabel lblNewLabel_2 = new JLabel("Accounts met 1 profiel");
		pnlOverview5Description.add(lblNewLabel_2);
			
		JScrollPane scrollPane_5 = new JScrollPane();
		pnlOverview5.add(scrollPane_5);
		
		tableOverview5 = new JTable();
		tableOverview5.setEnabled(false);
		tableOverview5.setFillsViewportHeight(true);
		scrollPane_5.setViewportView(tableOverview5);
		
		//---------------------------------------------------------------------------------------------------------------------------------------------
		//Creating the panel for Overview6
		//---------------------------------------------------------------------------------------------------------------------------------------------
		
		JPanel pnlOverview6 = new JPanel();
		tabbedPane.addTab("Overzicht 6", null, pnlOverview6, null);
		pnlOverview6.setLayout(new GridLayout(2, 0, 0, 0));
		
		JPanel pnlOverview6Form = new JPanel();
		pnlOverview6.add(pnlOverview6Form);
		pnlOverview6Form.setLayout(new GridLayout(6, 0, 0, 0));
		
		JPanel pnlOverview6FormRw1 = new JPanel();
		pnlOverview6Form.add(pnlOverview6FormRw1);
		pnlOverview6FormRw1.setLayout(new GridLayout(0, 6, 0, 0));
		
		JPanel pnlOverview6FormRw2 = new JPanel();
		pnlOverview6Form.add(pnlOverview6FormRw2);
		pnlOverview6FormRw2.setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel pnlOverview6Filler1 = new JPanel();
		pnlOverview6FormRw2.add(pnlOverview6Filler1);
		
		JLabel lblOverview6Movie = new JLabel("Selecteer een film");
		pnlOverview6FormRw2.add(lblOverview6Movie);
		lblOverview6Movie.setHorizontalAlignment(SwingConstants.CENTER);
		
		cbOverview6Movie = new JComboBox();
		pnlOverview6FormRw2.add(cbOverview6Movie);
		
		JPanel pnlOverview6FormRw3 = new JPanel();
		pnlOverview6Form.add(pnlOverview6FormRw3);
		pnlOverview6FormRw3.setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel pnlOverviewFiller3 = new JPanel();
		pnlOverview6FormRw3.add(pnlOverviewFiller3);
		
		JPanel pnlOverviewFiller2 = new JPanel();
		pnlOverview6FormRw3.add(pnlOverviewFiller2);
		
		//user selects their movie from the combobox
		//button event triggers when user clicks the button.
		//the movie that is selected at the moment of clicking will be used in the query.
		//the response of the query will be send to the table
		JButton btnOverview6Results = new JButton("Bekijk");
		pnlOverview6FormRw3.add(btnOverview6Results);
		btnOverview6Results.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int MovieId = (int) getKeyFromValue(mapMovieTitles, cbOverview6Movie.getSelectedItem().toString());
				String query = "SELECT Movie.Title as Titel, COUNT(Watched.ProfileId) AS 'Geheel bekeken'" + 
						" FROM Movie" + 
						" JOIN Watched on Movie.ProgramId = Watched.ProgramId" + 
						" WHERE Watched.PercentWatched = 100 and Movie.id = " + MovieId + 
						" GROUP BY Movie.Title ";
						tableOverview6.setModel(dbHandler.execute(query));
				
			}
		});
		
		JPanel pnlOverview6FormRw4 = new JPanel();
		pnlOverview6Form.add(pnlOverview6FormRw4);
		pnlOverview6FormRw4.setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel pnlOverview6FormRw5 = new JPanel();
		pnlOverview6Form.add(pnlOverview6FormRw5);
		pnlOverview6FormRw5.setLayout(new GridLayout(0, 6, 0, 0));
		
		JPanel pnlOverview6FormRw6 = new JPanel();
		pnlOverview6Form.add(pnlOverview6FormRw6);
		pnlOverview6FormRw6.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblOverview6Description = new JLabel("Aantal kijkers die de film volledig heeft gezien");
		lblOverview6Description.setHorizontalAlignment(SwingConstants.CENTER);
		pnlOverview6FormRw6.add(lblOverview6Description);
		
		JScrollPane scrollPane_6 = new JScrollPane();
		pnlOverview6.add(scrollPane_6);
		
		tableOverview6 = new JTable();
		tableOverview6.setEnabled(false);
		tableOverview6.setFillsViewportHeight(true);
		scrollPane_6.setViewportView(tableOverview6);
		
		//---------------------------------------------------------------------------------------------------------------------------------------------
		//Creating the panel for the info at the bottom of the page
		//---------------------------------------------------------------------------------------------------------------------------------------------
		
		JPanel pnlBottomInfo = new JPanel();
		frmNetflixStatistix.getContentPane().add(pnlBottomInfo, BorderLayout.SOUTH);
		pnlBottomInfo.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("    Netflix Statistix ");
		pnlBottomInfo.add(lblNewLabel, BorderLayout.WEST);
		
		JLabel lblNewLabel_1 = new JLabel("Informatica 2018 - 23IVT1A1A - Ricky, Nadia, Ayman    ");
		pnlBottomInfo.add(lblNewLabel_1, BorderLayout.EAST);
	}

	
	//small helper method to get the key from a value in a hashmap
	//might be moved to a new "UiHelper"class later
	public static Object getKeyFromValue(Map hm, Object value) {
	    for (Object o : hm.keySet()) {
	      if (hm.get(o).equals(value)) {
	        return o;
	      }
	    }
	    return null;
	  }
}
