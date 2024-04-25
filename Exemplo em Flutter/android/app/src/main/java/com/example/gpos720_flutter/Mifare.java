package com.example.gpos720_flutter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import br.com.gertec.easylayer.contactless.AuthenticationException;
import br.com.gertec.easylayer.contactless.ConnectionException;
import br.com.gertec.easylayer.contactless.GenericContactLess;
import br.com.gertec.easylayer.contactless.MifareClassic;
import br.com.gertec.gedi.GEDI;

public class Mifare extends AppCompatActivity {

    private TextView outputView, readBlocksText, authenticationKeyText;
    private MifareClassic mifare;
    private int DEFAULT_BLOCKS_TO_WRITE = 64;
    private int DEFAULT_DATA_VALUE_INDEX = 5;
    private int DEFAULT_RESTORE_INDEX = 6;
    private int BLOCKS_IN_SECTOR = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat); // Adicione esta linha antes do setContentView
        setContentView(R.layout.activity_mifare);


        GEDI.init(this);

        outputView = findViewById(R.id.outputView);
        authenticationKeyText = findViewById(R.id.authenticationKeyText);
        readBlocksText = findViewById(R.id.readBlocksText);

    }

    public void onReadBlocks(View view) {
        int blocksToRead = Integer.valueOf(readBlocksText.getText().toString());
        blocksToRead = blocksToRead > 64 ? 64 : blocksToRead;

        byte[] authenticationKey = Utils.hexStringToByteArray(authenticationKeyText.getText().toString());
        readBlocks(blocksToRead, authenticationKey);

    }


    private void readBlocks(int blocksToRead, byte[] authenticationKey) {
        mifare = new MifareClassic();
        byte[] cardId;
        byte[][] cardData = new byte[blocksToRead][MifareClassic.BLOCK_SIZE];

        try {
            cardId = mifare.connect(100000);
            for (int blockIndex = 0; blockIndex < blocksToRead; blockIndex++) {
                if (blockIndex % BLOCKS_IN_SECTOR == 0) {
                    int sector = mifare.blockToSector(blockIndex);
                    mifare.authenticateSectorWithKeyA(sector, authenticationKey);
                }
                cardData[blockIndex] = mifare.readBlock(blockIndex);
            }
            mifare.close();
            showCardData(cardId, cardData);
        } catch (ConnectionException e) {
            showError(e.getMessage());
            e.printStackTrace();
        } catch (AuthenticationException e) {
            showError(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            showError(e.getMessage());
            e.printStackTrace();
        }
    }

    private void showCardData(byte[] cardId, byte[][] cardData) {
        outputView.setText("Card id: " + Utils.byteArrayToHexString(cardId) + "\n");
        for (int blockIndex = 0; blockIndex < cardData.length; blockIndex++) {
            outputView.append("Block [" + blockIndex + "] : " + Utils.byteArrayToHexString(cardData[blockIndex]) + "\n");
        }
    }


    public void onSendCommand(View view) {

        GenericContactLess cardInterface = new GenericContactLess();

        // This command has been copied from Gedi_Cl test case (commandSendTest) from GEDI lib
        byte[] cmdData = {0x00, (byte) 0xa4, 0x04, 0x00, 0x0e, 0x32, 0x50, 0x41, 0x59, 0x2e, 0x53, 0x59, 0x53, 0x2e, 0x44, 0x44, 0x46, 0x30, 0x31, 0x00};
        byte[] cmdResp = null;

        try {
            cardInterface.connect(2000);

            //TODO validate expected 'cmdResp' response
            cmdResp = cardInterface.sendCommand(cmdData);

            cardInterface.close();
        } catch (ConnectionException | IOException e) {
            showError(e.getMessage());
        }

    }



    private void showError(String message) {
        outputView.setText("Error : " + message);
    }
}