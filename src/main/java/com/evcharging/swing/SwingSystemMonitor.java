package com.evcharging.swing;

import com.evcharging.util.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SwingSystemMonitor extends JFrame {

    // =====================================================
    // COLORS
    // =====================================================

    private static final Color BG =
            new Color(3, 10, 15);

    private static final Color SIDEBAR =
            new Color(4, 18, 27);

    private static final Color SIDEBAR_2 =
            new Color(6, 29, 40);

    private static final Color PANEL =
            new Color(5, 20, 29);

    private static final Color PANEL_2 =
            new Color(6, 27, 37);

    private static final Color BORDER =
            new Color(0, 170, 200);

    private static final Color CYAN =
            new Color(0, 220, 255);

    private static final Color GREEN =
            new Color(60, 240, 150);

    private static final Color PURPLE =
            new Color(170, 100, 255);

    private static final Color ORANGE =
            new Color(255, 190, 70);

    private static final Color RED =
            new Color(255, 80, 90);

    private static final Color TEXT =
            new Color(235, 250, 255);

    private static final Color MUTED =
            new Color(110, 145, 160);

    private static final Color HEADER =
            new Color(2, 15, 22);

    // =====================================================
    // DASHBOARD LABELS
    // =====================================================

    private JLabel usersLabel;
    private JLabel stationsLabel;
    private JLabel chargersLabel;
    private JLabel bookingsLabel;

    private JTable bookingTable;
    private DefaultTableModel tableModel;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public SwingSystemMonitor() {

        setTitle("SMART EV - System Monitor");

        setSize(
                1250,
                780
        );

        setMinimumSize(
                new Dimension(
                        1050,
                        680
                )
        );

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        createUI();

        loadDashboardData();
    }

    // =====================================================
    // MAIN UI
    // =====================================================

    private void createUI() {

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout()
                );

        mainPanel.setBackground(BG);

        // =================================================
        // LEFT SIDEBAR
        // =================================================

        JPanel sidebar =
                createSidebar();

        mainPanel.add(
                sidebar,
                BorderLayout.WEST
        );

        // =================================================
        // RIGHT CONTENT
        // =================================================

        JPanel content =
                new JPanel(
                        new BorderLayout(
                                18,
                                18
                        )
                );

        content.setBackground(BG);

        content.setBorder(
                new EmptyBorder(
                        24,
                        28,
                        24,
                        28
                )
        );

        // =================================================
        // HEADER
        // =================================================

        JPanel header =
                createHeader();

        content.add(
                header,
                BorderLayout.NORTH
        );

        // =================================================
        // CENTER
        // =================================================

        JPanel center =
                new JPanel(
                        new BorderLayout(
                                16,
                                16
                        )
                );

        center.setBackground(BG);

        // =================================================
        // STAT CARDS
        // =================================================

        JPanel statsPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                14,
                                0
                        )
                );

        statsPanel.setBackground(BG);

        usersLabel =
                createStatCard(
                        statsPanel,
                        "01",
                        "USERS",
                        "Registered accounts",
                        CYAN
                );

        stationsLabel =
                createStatCard(
                        statsPanel,
                        "02",
                        "STATIONS",
                        "Charging locations",
                        GREEN
                );

        chargersLabel =
                createStatCard(
                        statsPanel,
                        "03",
                        "CHARGERS",
                        "Installed infrastructure",
                        PURPLE
                );

        bookingsLabel =
                createStatCard(
                        statsPanel,
                        "04",
                        "BOOKINGS",
                        "Reservations in system",
                        ORANGE
                );

        center.add(
                statsPanel,
                BorderLayout.NORTH
        );

        // =================================================
        // RECENT BOOKINGS
        // =================================================

        JPanel bookingPanel =
                createBookingPanel();

        center.add(
                bookingPanel,
                BorderLayout.CENTER
        );

        content.add(
                center,
                BorderLayout.CENTER
        );

        // =================================================
        // FOOTER
        // =================================================

        JPanel footer =
                createFooter();

        content.add(
                footer,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                content,
                BorderLayout.CENTER
        );

        add(mainPanel);
    }

    // =====================================================
    // SIDEBAR
    // =====================================================

    private JPanel createSidebar() {

        JPanel sidebar =
                new JPanel(
                        new BorderLayout()
                );

        sidebar.setPreferredSize(
                new Dimension(
                        225,
                        0
                )
        );

        sidebar.setBackground(
                SIDEBAR
        );

        sidebar.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        0,
                        1,
                        new Color(
                                0,
                                90,
                                110
                        )
                )
        );

        // =================================================
        // SIDEBAR TOP
        // =================================================

        JPanel top =
                new JPanel(
                        new BorderLayout()
                );

        top.setBackground(
                SIDEBAR
        );

        top.setBorder(
                new EmptyBorder(
                        28,
                        20,
                        25,
                        20
                )
        );

        JLabel logo =
                new JLabel(
                        "⚡"
                );

        logo.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        38
                )
        );

        logo.setForeground(
                CYAN
        );

        JPanel logoText =
                new JPanel(
                        new GridLayout(
                                2,
                                1
                        )
                );

        logoText.setBackground(
                SIDEBAR
        );

        JLabel brand =
                new JLabel(
                        "SMART EV"
                );

        brand.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20
                )
        );

        brand.setForeground(
                TEXT
        );

        JLabel system =
                new JLabel(
                        "CONTROL CENTER"
                );

        system.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        9
                )
        );

        system.setForeground(
                CYAN
        );

        logoText.add(brand);
        logoText.add(system);

        top.add(
                logo,
                BorderLayout.WEST
        );

        top.add(
                logoText,
                BorderLayout.CENTER
        );

        sidebar.add(
                top,
                BorderLayout.NORTH
        );

        // =================================================
        // SIDEBAR MENU
        // =================================================

        JPanel menu =
                new JPanel();

        menu.setLayout(
                new BoxLayout(
                        menu,
                        BoxLayout.Y_AXIS
                )
        );

        menu.setBackground(
                SIDEBAR
        );

        menu.setBorder(
                new EmptyBorder(
                        5,
                        12,
                        10,
                        12
                )
        );

        JLabel section =
                new JLabel(
                        "MONITORING"
                );

        section.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        10
                )
        );

        section.setForeground(
                MUTED
        );

        section.setBorder(
                new EmptyBorder(
                        5,
                        12,
                        12,
                        5
                )
        );

        menu.add(section);

        // ACTIVE ITEM
        JPanel monitorItem =
                createSidebarItem(
                        "▣",
                        "System Monitor",
                        CYAN,
                        true
                );

        menu.add(
                monitorItem
        );

        menu.add(
                Box.createVerticalStrut(6)
        );

        JPanel gridItem =
                createSidebarItem(
                        "●",
                        "Grid Status",
                        GREEN,
                        false
                );

        menu.add(
                gridItem
        );

        menu.add(
                Box.createVerticalStrut(6)
        );

        JPanel energyItem =
                createSidebarItem(
                        "⚡",
                        "Energy Flow",
                        ORANGE,
                        false
                );

        menu.add(
                energyItem
        );

        menu.add(
                Box.createVerticalStrut(6)
        );

        JPanel chargerItem =
                createSidebarItem(
                        "◉",
                        "Chargers",
                        PURPLE,
                        false
                );

        menu.add(
                chargerItem
        );

        menu.add(
                Box.createVerticalStrut(6)
        );

        JPanel analyticsItem =
                createSidebarItem(
                        "▥",
                        "Analytics",
                        CYAN,
                        false
                );

        menu.add(
                analyticsItem
        );

        menu.add(
                Box.createVerticalGlue()
        );

        // =================================================
        // SIDEBAR STATUS BOX
        // =================================================

        JPanel statusBox =
                new JPanel(
                        new BorderLayout(
                                8,
                                6
                        )
                );

        statusBox.setBackground(
                SIDEBAR_2
        );

        statusBox.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        0,
                                        100,
                                        120
                                )
                        ),
                        new EmptyBorder(
                                12,
                                12,
                                12,
                                12
                        )
                )
        );

        JLabel statusTitle =
                new JLabel(
                        "● SYSTEM ONLINE"
                );

        statusTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        11
                )
        );

        statusTitle.setForeground(
                GREEN
        );

        JLabel statusText =
                new JLabel(
                        "<html>All services<br>operating normally</html>"
                );

        statusText.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        10
                )
        );

        statusText.setForeground(
                MUTED
        );

        statusBox.add(
                statusTitle,
                BorderLayout.NORTH
        );

        statusBox.add(
                statusText,
                BorderLayout.CENTER
        );

        menu.add(
                statusBox
        );

        sidebar.add(
                menu,
                BorderLayout.CENTER
        );

        // =================================================
        // SIDEBAR BOTTOM
        // =================================================

        JPanel bottom =
                new JPanel(
                        new BorderLayout()
                );

        bottom.setBackground(
                SIDEBAR
        );

        bottom.setBorder(
                new EmptyBorder(
                        15,
                        20,
                        20,
                        20
                )
        );

        JLabel version =
                new JLabel(
                        "SMART EV SYSTEM  v1.0"
                );

        version.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        9
                )
        );

        version.setForeground(
                MUTED
        );

        bottom.add(
                version,
                BorderLayout.WEST
        );

        sidebar.add(
                bottom,
                BorderLayout.SOUTH
        );

        return sidebar;
    }

    // =====================================================
    // SIDEBAR ITEM
    // =====================================================

    private JPanel createSidebarItem(
            String icon,
            String text,
            Color accent,
            boolean active
    ) {

        JPanel item =
                new JPanel(
                        new BorderLayout(
                                12,
                                0
                        )
                );

        item.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        48
                )
        );

        item.setPreferredSize(
                new Dimension(
                        195,
                        48
                )
        );

        item.setBorder(
                new EmptyBorder(
                        0,
                        12,
                        0,
                        12
                )
        );

        if (active) {

            item.setBackground(
                    SIDEBAR_2
            );

            item.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(
                                    0,
                                    3,
                                    0,
                                    0,
                                    accent
                            ),
                            new EmptyBorder(
                                    0,
                                    9,
                                    0,
                                    12
                            )
                    )
            );

        } else {

            item.setBackground(
                    SIDEBAR
            );
        }

        JLabel iconLabel =
                new JLabel(
                        icon
                );

        iconLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        17
                )
        );

        iconLabel.setForeground(
                accent
        );

        JLabel textLabel =
                new JLabel(
                        text
                );

        textLabel.setFont(
                new Font(
                        "Arial",
                        active
                                ? Font.BOLD
                                : Font.PLAIN,
                        12
                )
        );

        textLabel.setForeground(
                active
                        ? TEXT
                        : MUTED
        );

        item.add(
                iconLabel,
                BorderLayout.WEST
        );

        item.add(
                textLabel,
                BorderLayout.CENTER
        );

        // =================================================
        // HOVER EFFECT
        // =================================================

        item.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        if (!active) {

                            item.setBackground(
                                    SIDEBAR_2
                            );

                            textLabel.setForeground(
                                    TEXT
                            );
                        }
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        if (!active) {

                            item.setBackground(
                                    SIDEBAR
                            );

                            textLabel.setForeground(
                                    MUTED
                            );
                        }
                    }
                }
        );

        return item;
    }

    // =====================================================
    // HEADER
    // =====================================================

    private JPanel createHeader() {

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setBackground(
                BG
        );

        JPanel titlePanel =
                new JPanel(
                        new GridLayout(
                                2,
                                1
                        )
                );

        titlePanel.setBackground(
                BG
        );

        JLabel title =
                new JLabel(
                        "⚡ SYSTEM MONITOR"
                );

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        title.setForeground(
                CYAN
        );

        JLabel subtitle =
                new JLabel(
                        "REAL-TIME EV INFRASTRUCTURE MONITORING"
                );

        subtitle.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        subtitle.setForeground(
                MUTED
        );

        titlePanel.add(
                title
        );

        titlePanel.add(
                subtitle
        );

        header.add(
                titlePanel,
                BorderLayout.WEST
        );

        // =================================================
        // ONLINE STATUS
        // =================================================

        JPanel statusPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                1
                        )
                );

        statusPanel.setBackground(
                PANEL
        );

        statusPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                8,
                                18,
                                8,
                                18
                        )
                )
        );

        JLabel online =
                new JLabel(
                        "●  GRID ONLINE"
                );

        online.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        online.setForeground(
                GREEN
        );

        JLabel connection =
                new JLabel(
                        "JDBC / MYSQL • SYNCHRONIZED"
                );

        connection.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        9
                )
        );

        connection.setForeground(
                MUTED
        );

        statusPanel.add(
                online
        );

        statusPanel.add(
                connection
        );

        header.add(
                statusPanel,
                BorderLayout.EAST
        );

        return header;
    }

    // =====================================================
    // STAT CARD
    // =====================================================

    private JLabel createStatCard(
            JPanel parent,
            String number,
            String title,
            String description,
            Color accent
    ) {

        JPanel card =
                new JPanel(
                        new BorderLayout(
                                5,
                                4
                        )
                );

        card.setBackground(
                PANEL
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        0,
                                        90,
                                        110
                                )
                        ),
                        new EmptyBorder(
                                12,
                                15,
                                12,
                                15
                        )
                )
        );

        JLabel top =
                new JLabel(
                        number + "   " + title
                );

        top.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        10
                )
        );

        top.setForeground(
                accent
        );

        JLabel value =
                new JLabel(
                        "0"
                );

        value.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        31
                )
        );

        value.setForeground(
                TEXT
        );

        JLabel desc =
                new JLabel(
                        description
                );

        desc.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        9
                )
        );

        desc.setForeground(
                MUTED
        );

        card.add(
                top,
                BorderLayout.NORTH
        );

        card.add(
                value,
                BorderLayout.CENTER
        );

        card.add(
                desc,
                BorderLayout.SOUTH
        );

        parent.add(
                card
        );

        return value;
    }

    // =====================================================
    // BOOKING PANEL
    // =====================================================

    private JPanel createBookingPanel() {

        JPanel bookingPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                12
                        )
                );

        bookingPanel.setBackground(
                PANEL
        );

        bookingPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        0,
                                        105,
                                        130
                                )
                        ),
                        new EmptyBorder(
                                16,
                                18,
                                16,
                                18
                        )
                )
        );

        // =================================================
        // TABLE HEADER
        // =================================================

        JPanel tableHeader =
                new JPanel(
                        new BorderLayout()
                );

        tableHeader.setBackground(
                PANEL
        );

        JLabel recent =
                new JLabel(
                        "LIVE BOOKING STREAM"
                );

        recent.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        17
                )
        );

        recent.setForeground(
                TEXT
        );

        JLabel live =
                new JLabel(
                        "● REAL-TIME"
                );

        live.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12
                )
        );

        live.setForeground(
                GREEN
        );

        tableHeader.add(
                recent,
                BorderLayout.WEST
        );

        tableHeader.add(
                live,
                BorderLayout.EAST
        );

        bookingPanel.add(
                tableHeader,
                BorderLayout.NORTH
        );

        // =================================================
        // TABLE MODEL
        // =================================================

        tableModel =
                new DefaultTableModel(
                        new String[]{
                                "BOOKING",
                                "USER",
                                "STATION",
                                "CHARGER",
                                "DATE",
                                "DURATION",
                                "COST",
                                "STATUS"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {

                        return false;
                    }
                };

        bookingTable =
                new JTable(
                        tableModel
                );

        bookingTable.setRowHeight(
                32
        );

        bookingTable.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        bookingTable.setForeground(
                TEXT
        );

        bookingTable.setBackground(
                PANEL_2
        );

        bookingTable.setGridColor(
                new Color(
                        15,
                        55,
                        65
                )
        );

        bookingTable.setSelectionBackground(
                new Color(
                        0,
                        80,
                        95
                )
        );

        bookingTable.setSelectionForeground(
                Color.WHITE
        );

        bookingTable.setShowGrid(
                false
        );

        bookingTable.setIntercellSpacing(
                new Dimension(
                        0,
                        1
                )
        );

        bookingTable.setFillsViewportHeight(
                true
        );

        // =================================================
        // TABLE HEADER
        // =================================================

        bookingTable
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                38
                        )
                );

        bookingTable
                .getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                11
                        )
                );

        bookingTable
                .getTableHeader()
                .setForeground(
                        CYAN
                );

        bookingTable
                .getTableHeader()
                .setBackground(
                        HEADER
                );

        bookingTable
                .getTableHeader()
                .setOpaque(
                        true
                );

        // =================================================
        // TABLE CELL RENDERER
        // =================================================

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();

        renderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        renderer.setBackground(
                PANEL_2
        );

        renderer.setForeground(
                TEXT
        );

        for (
                int i = 0;
                i < bookingTable.getColumnCount();
                i++
        ) {

            bookingTable
                    .getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(
                            renderer
                    );
        }

        JScrollPane scrollPane =
                new JScrollPane(
                        bookingTable
                );

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scrollPane.getViewport()
                .setBackground(
                        PANEL_2
                );

        bookingPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        return bookingPanel;
    }

    // =====================================================
    // FOOTER
    // =====================================================

    private JPanel createFooter() {

        JPanel footer =
                new JPanel(
                        new BorderLayout()
                );

        footer.setBackground(
                BG
        );

        JLabel technology =
                new JLabel(
                        "JAVA SWING   •   JDBC   •   MYSQL"
                );

        technology.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        10
                )
        );

        technology.setForeground(
                MUTED
        );

        footer.add(
                technology,
                BorderLayout.WEST
        );

        JPanel buttons =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        buttons.setBackground(
                BG
        );

        JButton refreshButton =
                createButton(
                        "↻  REFRESH",
                        CYAN
                );

        refreshButton.addActionListener(
                e -> loadDashboardData()
        );

        JButton closeButton =
                createButton(
                        "CLOSE",
                        GREEN
                );

        closeButton.addActionListener(
                e -> dispose()
        );

        buttons.add(
                refreshButton
        );

        buttons.add(
                closeButton
        );

        footer.add(
                buttons,
                BorderLayout.EAST
        );

        return footer;
    }

    // =====================================================
    // BUTTON
    // =====================================================

    private JButton createButton(
            String text,
            Color accent
    ) {

        JButton button =
                new JButton(
                        text
                );

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        11
                )
        );

        button.setForeground(
                accent
        );

        button.setBackground(
                PANEL
        );

        button.setFocusPainted(
                false
        );

        button.setOpaque(
                true
        );

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                accent
                        ),
                        new EmptyBorder(
                                8,
                                18,
                                8,
                                18
                        )
                )
        );

        button.setPreferredSize(
                new Dimension(
                        120,
                        38
                )
        );

        return button;
    }

    // =====================================================
    // LOAD DASHBOARD
    // =====================================================

    private void loadDashboardData() {

        loadCounts();

        loadRecentBookings();
    }

    // =====================================================
    // LOAD COUNTERS
    // =====================================================

    private void loadCounts() {

        String usersSQL =
                "SELECT COUNT(*) FROM users";

        String stationsSQL =
                "SELECT COUNT(*) FROM stations";

        String chargersSQL =
                "SELECT COUNT(*) FROM chargers";

        String bookingsSQL =
                "SELECT COUNT(*) FROM bookings";

        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {

            usersLabel.setText(
                    String.valueOf(
                            getCount(
                                    connection,
                                    usersSQL
                            )
                    )
            );

            stationsLabel.setText(
                    String.valueOf(
                            getCount(
                                    connection,
                                    stationsSQL
                            )
                    )
            );

            chargersLabel.setText(
                    String.valueOf(
                            getCount(
                                    connection,
                                    chargersSQL
                            )
                    )
            );

            bookingsLabel.setText(
                    String.valueOf(
                            getCount(
                                    connection,
                                    bookingsSQL
                            )
                    )
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "DATABASE CONNECTION ERROR\n\n"
                            + e.getMessage(),
                    "Smart EV",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // GET COUNT
    // =====================================================

    private int getCount(
            Connection connection,
            String sql
    ) throws Exception {

        try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                sql
                        );

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                return resultSet.getInt(1);
            }
        }

        return 0;
    }

    // =====================================================
    // LOAD BOOKINGS
    // =====================================================

    private void loadRecentBookings() {

        tableModel.setRowCount(
                0
        );

        String sql =
                "SELECT booking_id, user_id, station_id, "
                        + "charger_id, booking_date, duration, "
                        + "cost, status "
                        + "FROM bookings "
                        + "ORDER BY booking_id DESC "
                        + "LIMIT 10";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql
                        );

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (
                    resultSet.next()
            ) {

                tableModel.addRow(
                        new Object[]{

                                resultSet.getInt(
                                        "booking_id"
                                ),

                                resultSet.getInt(
                                        "user_id"
                                ),

                                resultSet.getInt(
                                        "station_id"
                                ),

                                resultSet.getInt(
                                        "charger_id"
                                ),

                                resultSet.getString(
                                        "booking_date"
                                ),

                                resultSet.getInt(
                                        "duration"
                                ) + " min",

                                "₹"
                                        + String.format(
                                        "%.2f",
                                        resultSet.getDouble(
                                                "cost"
                                        )
                                ),

                                resultSet.getString(
                                        "status"
                                )
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "BOOKING DATA ERROR\n\n"
                            + e.getMessage(),
                    "Smart EV",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // OPEN MONITOR
    // =====================================================

    public static void showMonitor() {

        SwingUtilities.invokeLater(
                () -> {

                    SwingSystemMonitor monitor =
                            new SwingSystemMonitor();

                    monitor.setVisible(
                            true
                    );
                }
        );
    }
}