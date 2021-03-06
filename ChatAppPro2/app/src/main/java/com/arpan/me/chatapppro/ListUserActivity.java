package com.arpan.me.chatapppro;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.arpan.me.chatapppro.Adapter.ListUsersAdapter;
import com.arpan.me.chatapppro.Common.Common;
import com.arpan.me.chatapppro.Holder.QBUsersHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import de.measite.minidns.record.A;

public class ListUserActivity extends AppCompatActivity {
    ListView lstUsers;
    Button btnCreateChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);


        retriveAllUser();
        lstUsers = (ListView) findViewById(R.id.lstUsers);
        lstUsers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        btnCreateChat = (Button) findViewById(R.id.btn_create_chat);
        btnCreateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int CountChoice = lstUsers.getCount();
                if (lstUsers.getCheckedItemPositions().size()==1){
                    createPrivateChat(lstUsers.getCheckedItemPositions());



                }
                else if(lstUsers.getCheckedItemPositions().size()>1) {


                    createGroupChat(lstUsers.getCheckedItemPositions());
                }
                else
                    Toast.makeText(ListUserActivity.this,"Please select friend to chat",Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void createGroupChat(SparseBooleanArray checkedItemPositions) {

         final ProgressDialog mdialog = new ProgressDialog(ListUserActivity.this);
        mdialog.setMessage("Please waiting....");
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();

        int CountChoice = lstUsers.getCount();

        ArrayList<Integer> occupantIdsList = new ArrayList<>();
        for (int i=0;i<CountChoice;i++)
        {
            if (checkedItemPositions.get(i)){
                QBUser user = (QBUser)lstUsers.getItemAtPosition(i);
                occupantIdsList.add(user.getId());
            }

        }


        QBChatDialog dialog = new QBChatDialog();
        dialog.setName(Common.createChatDialogName(occupantIdsList));
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantIdsList);


        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                mdialog.dismiss();
                Toast.makeText(getBaseContext(),"Create chat dialog sucessfully",Toast.LENGTH_SHORT).show();

                finish();
            }

            @Override
            public void onError(QBResponseException e) {

                Log.e("Error",e.getMessage());
            }
        });
    }

    private void createPrivateChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog mdialog = new ProgressDialog(ListUserActivity.this);
        mdialog.setMessage("Please waiting....");
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.show();

             int CountChoice = lstUsers.getCount();
       // ArrayList<Integer> occupantIdsList = new ArrayList<>();
        for (int i=0;i<CountChoice;i++)
        {
            if (checkedItemPositions.get(i)){
                QBUser user = (QBUser)lstUsers.getItemAtPosition(i);
               // occupantIdsList.add(user.getId());
                QBChatDialog dialog = DialogUtils.buildPrivateDialog(user.getId());

                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {

                        mdialog.dismiss();
                        Toast.makeText(getBaseContext(),"Create private chat dialog sucessfully",Toast.LENGTH_SHORT).show();

                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

        }
        
    }

    private void retriveAllUser() {

      QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
          @Override
          public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

              QBUsersHolder.getInstance().putUsers(qbUsers);
              ArrayList<QBUser> qbUserWithoutCurrent = new ArrayList<QBUser>();
              for (QBUser user : qbUsers)
              {

                  if (!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin()))

                      qbUserWithoutCurrent.add(user);

              }

              ListUsersAdapter adapter = new ListUsersAdapter(getBaseContext(),qbUserWithoutCurrent);

              lstUsers.setAdapter(adapter);
              adapter.notifyDataSetChanged();


          }




          //@Override
        //  public void onError(QBResponseException e) {

         // }
    //  });

            @Override
            public void onError(QBResponseException e) {

                Log.e("ERROR",e.getMessage());
            }
        });

    }
}
