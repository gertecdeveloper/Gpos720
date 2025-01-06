package com.exampledv.gpos720_java.Impressora;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.exampledv.gpos720_java.R;

import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Alignment;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Status;
import br.com.gertec.gedi.exceptions.GediException;
import br.com.gertec.gedi.interfaces.ICL;
import br.com.gertec.gedi.interfaces.IGEDI;
import br.com.gertec.gedi.interfaces.IPRNTR;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_BarCodeConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_PictureConfig;

public class Printer extends AppCompatActivity {

    private EditText txtMensagemImpressao;

    private Button btnStatusImpressora, btnImagem, btnBarCode, btnImprimir;

    private IGEDI iGedi;
    private IPRNTR iPrint;
    private GEDI_PRNTR_e_Status status;


    IPRNTR iprntr;

    GEDI_PRNTR_e_Status prntrStatus;

    int prntrUsage;
    String gediVersion;
    br.com.gertec.gedi.enums.GEDI_PRNTR_e_BarCodeType GEDI_PRNTR_e_BarCodeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_printer);

        GEDI.init(this);

        new Thread(() -> {
            iGedi = GEDI.getInstance(Printer.this);
        }).start();

        btnStatusImpressora = findViewById(R.id.btnStatusImpressora);
        btnImprimir = findViewById(R.id.btnImprimir);
        btnImagem = findViewById(R.id.btnImagem);
        btnBarCode = findViewById(R.id.btnBarCode);



        btnStatusImpressora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iprntr = iGedi.getPRNTR();
                String GEDI_PRNTR_e_Status = null;
                try {


                    GEDI_PRNTR_e_Status = String.valueOf(iprntr.Status());

                    Log.v("status", GEDI_PRNTR_e_Status);


                    showMessagem("Status Impressora", GEDI_PRNTR_e_Status);
                } catch (GediException e) {
                    e.printStackTrace();
                }
                Log.d("Gertec: ", "Printer Status = " + GEDI_PRNTR_e_Status + " Paper Usage = " + GEDI_PRNTR_e_Status);
                //return traduzStatusImpressora(this.status);
            }

        });

        btnImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iGedi = GEDI.getInstance(Printer.this);
                iprntr = iGedi.getPRNTR();

                try {
                    GEDI_PRNTR_st_PictureConfig picconfig = new GEDI_PRNTR_st_PictureConfig();

                    //Align
                    picconfig.alignment = GEDI_PRNTR_e_Alignment.CENTER;
                    //Offset
                    picconfig.height = 50;
                    //Height
                    picconfig.height = 300;
                    //Width
                    picconfig.width = 300;
                    //Cor
//                    picconfig.color = true;  //(Desabilitei a configuração da cor e passou a funcionar)

                    //Print Data

                    Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.drawable.tef);

                    try {
                        //Desliga o módulo contactless para evitar conflito com a impressora
                        ICL mCL = iGedi.getCL();
                        mCL.PowerOff();
                        iprntr.Init();
                        iprntr.DrawPictureExt(picconfig,bmp);
                        iprntr.DrawBlankLine(50);
                        iprntr.Output();

                    } catch (GediException e) {
                        e.printStackTrace();
                        Log.v("Erro", "Ocorreu um erro na impressão da imagem");
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    Log.v("Erro", "Ocorreu um erro na impressão na config da imagem");
                }

            }
        });

        btnImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {

                    txtMensagemImpressao = findViewById(R.id.txtMensagemImpressao);

                    iprntr = iGedi.getPRNTR();

                    String GEDI_PRNTR_e_Status = String.valueOf(iprntr.Status());

                    Log.v("status", GEDI_PRNTR_e_Status);


                    tPRNTR.DrawString(Printer.this, iprntr, "CENTER", 20, 20,
                            "NORMAL", true, false, false, 20, txtMensagemImpressao.getText().toString());

                    iprntr.DrawBlankLine(50);

                } catch (GediException e) {
                    e.printStackTrace();
                }

            }
        });

        btnBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iGedi = GEDI.getInstance(Printer.this);
                iprntr = iGedi.getPRNTR();
                try {
                    GEDI_PRNTR_st_BarCodeConfig barCodeConfig = new GEDI_PRNTR_st_BarCodeConfig();


                    //Bar Code Type, use QR_CODE
                    barCodeConfig.barCodeType = GEDI_PRNTR_e_BarCodeType.QR_CODE;
                    //Height
                    barCodeConfig.height = 380;
                    //Width
                    barCodeConfig.width = 380;

                    //Print Data
                    try {
                        //Desliga o módulo contactless para evitar conflito com a impressora
                        ICL mCL = iGedi.getCL();
                        mCL.PowerOff();
                        iprntr.Init();
                        //Insira o texto que deseja imprimir
                        iprntr.DrawBarCode(barCodeConfig,"Print Custom String");
                        iprntr.DrawBlankLine(80);
                        iprntr.Output();
                    } catch (GediException e) {
                        e.printStackTrace();
                        Log.v("Erro", "Ocorreu um erro na impressão do QR texto");
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    Log.v("Erro", "Ocorreu um erro na impressãodo QR configuração");
                }
            }
        });




    }


    protected void showMessagem(String titulo, String mensagem){
        AlertDialog alertDialog = new AlertDialog.Builder(Printer.this).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensagem);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    void ShowFalha(String sStatus){
        showMessagem("Falha Impressor",sStatus);
    }
}