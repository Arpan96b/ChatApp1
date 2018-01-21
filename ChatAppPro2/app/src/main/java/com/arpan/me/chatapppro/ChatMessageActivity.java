package com.arpan.me.chatapppro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.arpan.me.chatapppro.Adapter.ChatMessageAdapter;
import com.arpan.me.chatapppro.Common.Common;
import com.arpan.me.chatapppro.Holder.QBChatMessageHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;

public class ChatMessageActivity extends AppCompatActivity {

    QBChatDialog qbChatDialog;
    ListView lstChatMessage;
    ImageButton submitButton;
    EditText editContent;
    ChatMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        initViews();
        initChatDialog();
        retriveMessage();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QBChatMessage ChatMessage = new QBChatMessage();
                ChatMessage.setBody(editContent.getText().toString());
                ChatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                ChatMessage.setSaveToHistory(true);


                try {
                    qbChatDialog.sendMessage(ChatMessage);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }

                QBChatMessageHolder.getInstance().putMessage(qbChatDialog.getDialogId(),ChatMessage);


                ArrayList<QBChatMessage> messages = QBChatMessageHolder.getInstance().qbChatMessagesByDialogId(qbChatDialog.getDialogId());

                adapter = new ChatMessageAdapter(getBaseContext(),messages);
                lstChatMessage.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                editContent.setText("");
                editContent.setFocusable(true);

            }
        });

    }

    private void retriveMessage() {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(500); //get limit 500 messages

        if (qbChatDialog != null){

            QBRestChatService.getDialogMessages(qbChatDialog,messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    QBChatMessageHolder.getInstance().putMessage(qbChatDialog.getDialogId(),qbChatMessages);
                    adapter = new ChatMessageAdapter(getBaseContext(),qbChatMessages);
                    lstChatMessage.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onError(QBResponseException e) {

                }
            });

        }
    }

    private void initChatDialog() {

        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(Common.DIALOG_EXTRA);
        qbChatDialog.initForChat(QBChatService.getInstance());
        QBIncomingMessagesManager incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();

        incomingMessagesManager.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

            }
        });
        qbChatDialog.addMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                QBChatMessageHolder.getInstance().putMessage(qbChatMessage.getDialogId(),qbChatMessage);

                ArrayList<QBChatMessage> messages = QBChatMessageHolder.getInstance().qbChatMessagesByDialogId(qbChatMessage.getDialogId());
                adapter = new ChatMessageAdapter(getBaseContext(),messages);
                lstChatMessage.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {


                Log.e("ERROR",e.getMessage());
            }
        });
    }

    private void initViews() {
        lstChatMessage = (ListView) findViewById(R.id.list_of_message);
        submitButton = (ImageButton) findViewById(R.id.send_button);
        editContent = (EditText) findViewById(R.id.edt_content);

    }
}
