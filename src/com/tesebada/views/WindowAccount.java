package com.tesebada.views;

import com.tesebada.database.Cheque;
import com.tesebada.database.ChequesRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Optional;

public class WindowAccount extends JFrame {
    public static String USERNAME;


    private JPanel PanelCenter, PanelTop,PanelBottom;
    private JTableHeader TableHeader;

    boolean flag =  false;
    private JScrollPane ScrollTable;
    private JTable Table;
    private ChequesRepository chequesRepository = new ChequesRepository();

    private DefaultTableModel TableModel;

    private JTextField tfnoCuenta, tfretiro;

    private JLabel labelError;
    private JButton btnRetiro,btnReporte;

    private Object [] data;

    public WindowAccount(String responsable) {
        this.USERNAME = responsable;
        init();
        setListeners();
    }

    private void setListeners(){
        this.btnRetiro.addActionListener(event -> startRestCicle());
        this.btnReporte.addActionListener( event -> showStatusAccount());
    }

    private void showStatusAccount(){
       new Reporte();
    }

    private void startRestCicle(){
        String _chequera = tfnoCuenta.getText();
        String _retiro = tfretiro.getText();
        labelError.setText("");

        if(!flag) {

            if (_chequera.isEmpty() || _retiro.isEmpty()) {
                labelError.setText("Datos no validos");
                return;
            }

            Optional<Cheque> cheque = chequesRepository.getChequeV2(_chequera, Integer.parseInt(_retiro));
            cheque.ifPresent(valor -> {
                data = new Object[]{ valor.getNoCuenta(), valor.getImporte(), valor.getEstatus() };
                TableModel.addRow(data);
                Table.update(Table.getGraphics());

            });

            if (!cheque.isPresent()) {
                labelError.setText("No existe la cuenta ingresada");
                return;
            }


            flag = true;
            tfretiro.setEnabled(true);
        } else {

            for (int i = 0; i < 10 ; i++) {
                Optional<Cheque> cheque = chequesRepository.getChequeV2(_chequera, Integer.parseInt(_retiro));
                cheque.ifPresent(valor -> {

                    data = new Object[]{valor.getNoCuenta(), valor.getImporte(), valor.getEstatus()};
                    TableModel.addRow(data);
                    this.revalidate();
                    this.update(this.getGraphics());
                    this.repaint();

                });

                if (!cheque.isPresent()) {
                    labelError.setText("No existe la cuenta ingresada");
                    return;
                }
            }

        }

    }

    private void init() {
        this.setSize(560, 500);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PanelCenter = new JPanel();
        PanelTop    = new JPanel();
        PanelBottom = new JPanel();

        TableModel  = new DefaultTableModel(new String[]{"No. Cuenta", "Importe", "Estatus"}, 0);
        Table       = new JTable(TableModel);
        ScrollTable = new JScrollPane(Table);
        TableHeader = Table.getTableHeader();

        btnRetiro   = new JButton("Retirar");
        btnReporte  = new JButton("Reporte");
        labelError  = new JLabel();
        tfnoCuenta  = new JTextField();
        tfretiro    = new JTextField();

        PanelCenter.setLayout(new BorderLayout());
        PanelTop.setLayout(new GridLayout(0,3));

        labelError.setForeground(Color.red);
        tfretiro.setText("0");
        tfretiro.setEnabled(false);



        PanelTop.add(new JLabel("No. Cuenta"));
        PanelTop.add(tfnoCuenta);
        PanelTop.add(labelError);
        PanelTop.add(new JLabel("Cantidad $: "));
        PanelTop.add(tfretiro);
        PanelTop.add(btnRetiro);

        PanelCenter.add(TableHeader, BorderLayout.NORTH);
        PanelCenter.add(ScrollTable, BorderLayout.CENTER);

        PanelBottom.add(btnReporte);

        this.add(PanelCenter, BorderLayout.CENTER);
        this.add(PanelTop, BorderLayout.NORTH);
        this.add(PanelBottom,BorderLayout.SOUTH);

        this.setVisible(true);

    }

}
