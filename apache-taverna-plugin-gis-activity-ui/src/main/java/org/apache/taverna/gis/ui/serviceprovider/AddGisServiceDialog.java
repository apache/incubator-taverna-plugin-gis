package org.apache.taverna.gis.ui.serviceprovider;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;
import org.apache.taverna.gis.client.GisClientFactory;
import org.apache.taverna.gis.client.IGisClient;

import net.sf.taverna.t2.workbench.helper.HelpEnabledDialog;

@SuppressWarnings("serial")
public class AddGisServiceDialog extends HelpEnabledDialog {

	private JTextField serviceLocationField;
	private TableRowSorter<DefaultTableModel> sorter;
	private JFilterTable processesTable;
	
	private Logger logger = Logger.getLogger(AddGisServiceDialog.class);
	
	public AddGisServiceDialog(Frame frame) {
		// null will call MainWindow.getMainWindow()
		super(frame, "Add WPS service", true, null); // create a non-modal dialog
		initComponents();
	}

	private void initComponents() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel servicePanel = new JPanel(new GridBagLayout());
		servicePanel.setBorder(BorderFactory.createTitledBorder("Select Web Processing Service"));

		JPanel processPanel = new JPanel(new GridBagLayout());
		processPanel.setBorder(BorderFactory.createTitledBorder("Select Processes"));

		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		GridBagConstraints gbc = new GridBagConstraints();

		// add service panels
		gbc.weighty = 0;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(0, 0, 5, 0);

		mainPanel.add(servicePanel, gbc);

		// add process panel
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 0, 0, 0);

		mainPanel.add(processPanel, gbc);

		// add buttons panel
		gbc.weighty = 0;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 0, 0, 0);

		mainPanel.add(buttonsPanel, gbc);

		initServicePanel(servicePanel);
		initProcessPanel(processPanel);
		initButtonsPanel(buttonsPanel);

		getContentPane().add(mainPanel);

		setMinimumSize(new Dimension(500, 500));
	}

	private void initServicePanel(JPanel servicePanel) {
		GridBagConstraints gbc = new GridBagConstraints();

		// add service url
		JLabel serviceLocatitionLabel = new JLabel("Service URL", JLabel.LEFT);
		gbc.weighty = 0;
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 10, 0, 0);

		servicePanel.add(serviceLocatitionLabel, gbc);

		serviceLocationField = new JTextField("http://somehost/service?");
		gbc.weighty = 0;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 10, 5, 5);

		servicePanel.add(serviceLocationField, gbc);

		JButton serviceLocationButton = new JButton("Search");
		gbc.weighty = 0;
		gbc.weightx = 0;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(0, 10, 5, 5);

		serviceLocationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				String url = serviceLocationField.getText();
				
				url = url.trim();
				
				serviceLocationField.setText(url);
				
				if (isValidURL(url))
				{
					setProcessTableData(url);
				}
			}
		});
		
		servicePanel.add(serviceLocationButton, gbc);
	}

	private void initProcessPanel(JPanel processPanel) {
		GridBagConstraints gbc = new GridBagConstraints();

		// Add Process Filter
		JLabel processFilterLabel = new JLabel("Filter", JLabel.LEFT);
		gbc.weighty = 0;
		gbc.weightx = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 10, 0, 0);

		processPanel.add(processFilterLabel, gbc);

		JTextField processFilterField = createFilterTextField();
		gbc.weighty = 0;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 10, 5, 5);

		processPanel.add(processFilterField, gbc);

		JPanel selectOptions = new JPanel(new GridBagLayout());

		// Add Select/Deselect All Buttons
		JButton selectAll = createUrlButton("Select All");

		selectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				checkAllProcesses(true);
			}
		});

		gbc.weighty = 0;
		gbc.weightx = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 10, 5, 5);

		selectOptions.add(selectAll, gbc);

		JButton deselectAll = createUrlButton("Deselect All");

		deselectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				checkAllProcesses(false);
			}
		});

		gbc.weighty = 0;
		gbc.weightx = 0.5;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 10, 5, 5);

		selectOptions.add(deselectAll, gbc);

		gbc.weighty = 0;
		gbc.weightx = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 10, 5, 5);

		processPanel.add(selectOptions, gbc);

		// Add Processes List

		initProcessesTable();
		
		JScrollPane scrollPane = new JScrollPane(processesTable);

		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 10, 5, 5);

		processPanel.add(scrollPane, gbc);
	}

	private void initButtonsPanel(JPanel buttonsPanel) {
		// add service buttons
		final JButton addServiceButton = new JButton("Add");
		addServiceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				addPressed();
			}
		});

		// When user presses "Return" key fire the action on the "Add" button
		addServiceButton.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
					addPressed();
				}
			}
		});

		final JButton cancelServiceButton = new JButton("Cancel");
		cancelServiceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				closeDialog();
			}
		});

		getRootPane().setDefaultButton(addServiceButton);

		buttonsPanel.add(addServiceButton);
		buttonsPanel.add(cancelServiceButton);

		// TODO: Set same button size
		addServiceButton.setMaximumSize(cancelServiceButton.getSize());

	}
	
	private void initProcessesTable()
	{
		// setup columns
		Vector<String> columnNames = new Vector<String>();
		columnNames.addElement(""); 
		columnNames.addElement("Processes");
		
		Vector<ProcessTableRow> data = null;

		DefaultTableModel model = new DefaultTableModel(data, columnNames) {

			@Override
			public boolean isCellEditable(int row, int column) {
				// Only the first column
				return column == 0;
			}

		};

		sorter = new TableRowSorter<DefaultTableModel>(model);

		processesTable = new JFilterTable(model);
		processesTable.setRowSorter(sorter);

		processesTable.getColumnModel().getColumn(0).setPreferredWidth(20);
		processesTable.getColumnModel().getColumn(1).setPreferredWidth(250);

		processesTable.setPreferredScrollableViewportSize(new Dimension(300, 100));
		processesTable.setFillsViewportHeight(true);
		processesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
	}

	private void setProcessTableData(String serviceURL)
	{
		DefaultTableModel model = (DefaultTableModel) processesTable.getModel();
		
		// discard existing rows
		model.setRowCount(0);

		List<ProcessTableRow> data = getServiceProcesses(serviceURL);
		
		for(ProcessTableRow iterator : data)
		{
			Object[] newRow = { iterator.isChecked(), iterator.getProcessID() };
			model.addRow(newRow);
		}
		
		model.fireTableDataChanged();
		
	}
	
	private List<ProcessTableRow> getServiceProcesses(String serviceURL)
	{
		List<ProcessTableRow> result = new ArrayList<ProcessTableRow>();
		
		IGisClient gisClient = GisClientFactory.getInstance().getGisClient(serviceURL);
		
		List<String> servicesList = null;
		
		try {
			servicesList = gisClient.getProcessList();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null,
					"Could not read the service definition from "
							+ serviceLocationField.getText() + ":\n" + ex,
					"Could not add service service",
					JOptionPane.ERROR_MESSAGE);

			logger.error(
					"Failed to list GWS processes for service: "
							+ serviceLocationField.getText(), ex);
		}
		
		for(String process : servicesList)
		{
			result.add(new ProcessTableRow(true, process));
		}
		
		return result;
		
	}
	
	private void checkAllProcesses(boolean value) {
		for (int i = 0; i < processesTable.getRowCount(); i++)
			processesTable.getModel().setValueAt(value, i, 0);
	}

	private void addPressed() {
		final String serviceURL = serviceLocationField.getText().trim();
		new Thread("Adding Service " + serviceURL) {
			public void run() {
				try {
					
					URL url = new URL(serviceURL);
					URLConnection connection = url.openConnection();
					
					try {
						connection.connect(); // if this does not fail - add the service
					} finally {
						try {
							connection.getInputStream().close();
						} catch (IOException ex) {
						}
					}
					
				} catch (Exception ex) { // anything failed
					JOptionPane.showMessageDialog(null,
							"Could not read the service definition from "
									+ serviceLocationField.getText() + ":\n" + ex,
							"Could not add service service",
							JOptionPane.ERROR_MESSAGE);

					logger.error(
							"Failed to list GWS processes for service: "
									+ serviceLocationField.getText(), ex);
				}
			};
		}.start();
		closeDialog();

	}

	private void closeDialog() {
		setVisible(false);
		dispose();
	}
	
	private JTextField createFilterTextField() {
		final JTextField field = new JTextField();

		final int FILTER_COLUMN_INDEX = 1;

		field.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				filterProcesses(field.getText(), FILTER_COLUMN_INDEX);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				filterProcesses(field.getText(), FILTER_COLUMN_INDEX);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				filterProcesses(field.getText(), FILTER_COLUMN_INDEX);
			}

		});

		return field;

	}

	private void filterProcesses(String criteria, int columnIndex) {
		RowFilter<DefaultTableModel, Object> rowFilter = null;

		try {
			rowFilter = RowFilter.regexFilter("(?i)" + criteria, columnIndex);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}

		sorter.setRowFilter(rowFilter);

	}

	private boolean isValidURL(String url)
	{
		
		boolean result = true;
		
		url = url.trim();
		
		if (url.length()<=0)
			return false;
		
		// other url checks
		
		return result;
		
	}
	
	private JButton createUrlButton(String buttonText) {
		JButton button = new JButton();
		button.setText("<HTML><FONT color=\"#000099\"><U>" + buttonText + "</U></FONT></HTML>");
		button.setHorizontalAlignment(SwingConstants.CENTER);
		button.setBorderPainted(false);
		button.setOpaque(false);
		button.setBackground(Color.WHITE);

		return button;
	}

	public class JFilterTable extends JTable {

		public JFilterTable(DefaultTableModel model) {
			super(model);
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Class getColumnClass(int column) {
			switch (column) {
			case 0:
				return Boolean.class;
			case 1:
				return String.class;
			default:
				return String.class;
			}
		}
	};
	
	public class ProcessTableRow {
		private boolean isChecked;
		private String processID;
		
		public ProcessTableRow()
		{
			
		}
		
		public ProcessTableRow(boolean isChecked, String processID)
		{
			this.isChecked = isChecked;
			this.processID = processID;
		}
		
		public boolean isChecked() {
			return isChecked;
		}
		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}
		public String getProcessID() {
			return processID;
		}
		public void setProcessID(String processID) {
			this.processID = processID;
		}
	}

}
