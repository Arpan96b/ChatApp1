package com.arpan.me.chatapppro.Holder;

import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by me on 1/13/2018.
 */

public class QBChatMessageHolder   {

    private  static QBChatMessageHolder instance;
    private HashMap<String,ArrayList<QBChatMessage>> qbChatMessageArray;

    public static synchronized QBChatMessageHolder getInstance(){
        QBChatMessageHolder qbChatMessageHolder;
        synchronized (QBChatMessageHolder.class){
            if (instance==null)
                instance = new QBChatMessageHolder();
                qbChatMessageHolder = instance;

            return qbChatMessageHolder;
        }
    }

    private QBChatMessageHolder(){
        this.qbChatMessageArray = new HashMap<>();
    }

    public void putMessage(String dialodId, ArrayList<QBChatMessage> qbChatMessages){

        this.qbChatMessageArray.put(dialodId,qbChatMessages);
    }

        public void putMessage(String dialogId,QBChatMessage qbChatMessage){

            List<QBChatMessage> lstResult =(List)this.qbChatMessageArray.get(dialogId);

            lstResult.add(qbChatMessage);
            ArrayList<QBChatMessage> lstadded =new ArrayList(lstResult.size());
                    lstadded.addAll(lstResult);

                    putMessage(dialogId,lstadded);
        }

        public ArrayList<QBChatMessage> qbChatMessagesByDialogId(String dialogId){

            return (ArrayList<QBChatMessage>)this.qbChatMessageArray.get(dialogId);
        }
}
