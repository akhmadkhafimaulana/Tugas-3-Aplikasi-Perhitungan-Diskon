import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DiscountCalculatorApp extends JFrame {
    // Components
    private JLabel lblTitle;
    private JLabel lblHargaAsli;
    private JTextField txtHargaAsli;
    private JLabel lblPersentase;
    private JComboBox<String> cmbDiskon;
    private JLabel lblAtur;
    private JSlider sldDiskon;
    private JLabel lblSliderValue;
    private JLabel lblKupon;
    private JTextField txtKupon;
    private JButton btnHitung;
    private JLabel lblHargaAkhir;
    private JTextField txtHargaAkhir;
    private JLabel lblHemat;
    private JTextField txtHemat;
    private JLabel lblRiwayat;
    private JTextArea txtRiwayat;
    private JScrollPane scrollRiwayat;

    // coupon map
    private HashMap<String, Integer> couponMap;

    // currency formatter
    private NumberFormat currencyFormat;

    public DiscountCalculatorApp() {
        initCoupons();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id","ID")); // Indonesian format
        initComponents();
        setupLayout();
        setupListeners();
        setTitle("Aplikasi Perhitungan Diskon - Lengkap");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 620);
        setLocationRelativeTo(null);
    }

    private void initCoupons() {
        couponMap = new HashMap<>();
        couponMap.put("HEMAT10", 10);
        couponMap.put("SALE5", 5);
        couponMap.put("BIG20", 20);
        // tambah kode kupon lain sesuai kebutuhan
    }

    private void initComponents() {
        lblTitle = new JLabel("Aplikasi Perhitungan Diskon", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));

        lblHargaAsli = new JLabel("Harga Asli (Rp):");
        txtHargaAsli = new JTextField();

        lblPersentase = new JLabel("Persentase Diskon:");
        String[] diskonOptions = {"0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%", "Custom"};
        cmbDiskon = new JComboBox<>(diskonOptions);
        cmbDiskon.setSelectedIndex(2); // default 10%

        lblAtur = new JLabel("Atur Diskon Manual:");
        sldDiskon = new JSlider(0, 90, 10); // max 90% untuk keamanan (bisa diubah)
        sldDiskon.setMajorTickSpacing(10);
        sldDiskon.setPaintTicks(true);
        sldDiskon.setPaintLabels(true);

        lblSliderValue = new JLabel("Diskon: 10%");

        lblKupon = new JLabel("Kode Kupon (Opsional):");
        txtKupon = new JTextField();

        btnHitung = new JButton("HITUNG");

        lblHargaAkhir = new JLabel("Harga Setelah Diskon:");
        txtHargaAkhir = new JTextField();
        txtHargaAkhir.setEditable(false);

        lblHemat = new JLabel("Total Penghematan:");
        txtHemat = new JTextField();
        txtHemat.setEditable(false);

        lblRiwayat = new JLabel("Riwayat Perhitungan:");
        txtRiwayat = new JTextArea();
        txtRiwayat.setEditable(false);
        txtRiwayat.setRows(8);
        txtRiwayat.setLineWrap(true);
        txtRiwayat.setWrapStyleWord(true);
        scrollRiwayat = new JScrollPane(txtRiwayat);
    }

    private void setupLayout() {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Row 0: title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        p.add(lblTitle, gbc);

        // Row 1: Harga Asli
        gbc.gridy++; gbc.gridwidth = 1;
        p.add(lblHargaAsli, gbc);
        gbc.gridx = 1;
        p.add(txtHargaAsli, gbc);

        // Row 2: Persentase Combo
        gbc.gridx = 0; gbc.gridy++;
        p.add(lblPersentase, gbc);
        gbc.gridx = 1;
        p.add(cmbDiskon, gbc);

        // Row 3: Kupon
        gbc.gridx = 0; gbc.gridy++;
        p.add(lblKupon, gbc);
        gbc.gridx = 1;
        p.add(txtKupon, gbc);

        // Row 4: Slider label
        gbc.gridx = 0; gbc.gridy++;
        p.add(lblAtur, gbc);
        gbc.gridx = 1;
        p.add(lblSliderValue, gbc);

        // Row 5: Slider full width
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        p.add(sldDiskon, gbc);

        // Row 6: button center
        gbc.gridy++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnHitung);
        p.add(btnPanel, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 7: Harga Akhir
        gbc.gridy++; gbc.gridwidth = 1; gbc.gridx = 0;
        p.add(lblHargaAkhir, gbc);
        gbc.gridx = 1;
        p.add(txtHargaAkhir, gbc);

        // Row 8: Hemat
        gbc.gridx = 0; gbc.gridy++;
        p.add(lblHemat, gbc);
        gbc.gridx = 1;
        p.add(txtHemat, gbc);

        // Row 9: Riwayat label
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        p.add(lblRiwayat, gbc);

        // Row 10: Riwayat area
        gbc.gridy++; gbc.weighty = 1; gbc.fill = GridBagConstraints.BOTH;
        p.add(scrollRiwayat, gbc);

        getContentPane().add(p);
    }

    private void setupListeners() {
        // ComboBox select -> sync slider (except "Custom")
        cmbDiskon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sel = (String) cmbDiskon.getSelectedItem();
                if (sel != null && !sel.equals("Custom")) {
                    sel = sel.replace("%","");
                    try {
                        int val = Integer.parseInt(sel);
                        sldDiskon.setValue(val);
                        lblSliderValue.setText("Diskon: " + val + "%");
                    } catch (NumberFormatException ex) {
                        // ignore
                    }
                }
            }
        });

        // Slider -> set combo to Custom and update label
        sldDiskon.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int v = sldDiskon.getValue();
                lblSliderValue.setText("Diskon: " + v + "%");
                // if slider value matches one of the combo entries, select it, else "Custom"
                boolean matched = false;
                for (int i=0;i<cmbDiskon.getItemCount();i++) {
                    String item = cmbDiskon.getItemAt(i);
                    if (!item.equals("Custom") && item.replace("%","").equals(String.valueOf(v))) {
                        cmbDiskon.setSelectedIndex(i);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    cmbDiskon.setSelectedItem("Custom");
                }
            }
        });

        // Button Hitung -> main logic
        btnHitung.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doCalculate();
            }
        });

        // Enter on harga field triggers calculate
        txtHargaAsli.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doCalculate();
            }
        });
    }

    private void doCalculate() {
        String hargaStr = txtHargaAsli.getText().trim();
        if (hargaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan harga asli terlebih dahulu.", "Input kosong", JOptionPane.WARNING_MESSAGE);
            txtHargaAsli.requestFocus();
            return;
        }

        // Remove non-digit except comma/dot
        hargaStr = hargaStr.replaceAll("[^0-9.,]", "");
        hargaStr = hargaStr.replace(",", ""); // remove thousand separators if user typed
        double harga;
        try {
            harga = Double.parseDouble(hargaStr);
            if (harga < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Masukkan harga berupa angka yang valid.", "Input tidak valid", JOptionPane.ERROR_MESSAGE);
            txtHargaAsli.requestFocus();
            return;
        }

        // base diskon: read from slider (slider selalu valid)
        int baseDiskon = sldDiskon.getValue();

        // coupon
        String kodeKupon = txtKupon.getText().trim().toUpperCase();
        int couponBonus = 0;
        if (!kodeKupon.isEmpty()) {
            if (couponMap.containsKey(kodeKupon)) {
                couponBonus = couponMap.get(kodeKupon);
            } else {
                int res = JOptionPane.showConfirmDialog(this,
                        "Kode kupon tidak dikenal. Lanjutkan tanpa coupon?",
                        "Kupon tidak valid",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (res == JOptionPane.NO_OPTION) {
                    txtKupon.requestFocus();
                    return;
                }
            }
        }

        // total discount (cap it to 90% to prevent negative)
        double totalDiskon = baseDiskon + couponBonus;
        if (totalDiskon > 90) totalDiskon = 90;

        // calculation
        double penghematan = harga * (totalDiskon / 100.0);
        double hargaAkhir = harga - penghematan;

        // format and display
        txtHargaAkhir.setText(currencyFormat.format(hargaAkhir));
        txtHemat.setText(currencyFormat.format(penghematan));

        // append to history with timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String waktu = sdf.format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(waktu).append("] ");
        sb.append("Harga: ").append(currencyFormat.format(harga));
        sb.append(", Diskon: ").append(String.format("%.2f", totalDiskon)).append("%");
        if (!kodeKupon.isEmpty()) {
            sb.append(" (Kupon: ").append(kodeKupon).append(")");
        }
        sb.append(", Akhir: ").append(currencyFormat.format(hargaAkhir));
        sb.append("\n");
        txtRiwayat.append(sb.toString());
    }

    // main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DiscountCalculatorApp app = new DiscountCalculatorApp();
            app.setVisible(true);
        });
    }
}
