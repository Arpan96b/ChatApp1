package com.arpan.me.chatapppro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.arpan.me.chatapppro.Adapter.ChatDialogAdapter;
import com.arpan.me.chatapppro.Common.Common;
import com.arpan.me.chatapppro.Holder.QBUsersHolder;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class ChatDialogActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    ListView lstChatDialog;

    @Override
    protected void onResume() {
        super.onResume();
        loadChatDialog();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialog);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.chatdialog_adduser);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDialogActivity.this,ListUserActivity.class);
                startActivity(intent);

            }
        });

        createSessioForChat();

        lstChatDialog = (ListView) findViewById(R.id.lstChatDialogs);
        lstChatDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QBChatDialog qbChatDialog =  (QBChatDialog)lstChatDialog.getAdapter().getItem(position);
                Intent intent = new Intent(ChatDialogActivity.this,ChatMessageActivity.class);
                intent.putExtra(Common.DIALOG_EXTRA,qbChatDialog);
                startActivity(intent);

            }
        });
        loadChatDialog();

    }

    private void loadChatDialog() {


        QBRequestGetBuilder qbRequestGetBuilder = new QBRequestGetBuilder();

        qbRequestGetBuilder.setLimit(100);
        QBRestChatService.getChatDialogs(null,qbRequestGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {


                ChatDialogAdapter adapter = new ChatDialogAdapter(getBaseContext(),qbChatDialogs);

                lstChatDialog.setAdapter(adapter);
                adapter.notifyDataSetChanged();



            }

            @Override
            public void onError(QBResponseException e) {

                Log.e("Error",e.getMessage());
            }
        });
    }

    private void createSessioForChat() {

        final ProgressDialog mdialog = new ProgressDialog(ChatDialogActivity.this);
        mdialog.setMessage("Please waiting...");
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();

        String user, password;
        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");

        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                QBUsersHolder.getInstance().putUsers(qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

        final QBUser qbUser = new QBUser(user,password);
        QBAuth.createSession(qbUser).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());

                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }
                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        mdialog.dismiss();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                        Log.e("ERROR",""+e.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {



            }
        });



    }
}
