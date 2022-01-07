package be.heh.fitdevoie.projetandroidstudio.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import be.heh.fitdevoie.projetandroidstudio.Database.User;
import be.heh.fitdevoie.projetandroidstudio.R;

public class Adapter extends BaseAdapter {

    Context context;
    ArrayList<User> userList;
    private static LayoutInflater inflater = null;

    public Adapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public User getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return userList.get(position).getUserId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(vi == null) {
            vi = inflater.inflate(R.layout.row_authorize_user, null);
        }
        TextView tv_row_userId = (TextView) vi.findViewById(R.id.tv_row_userId);
        tv_row_userId.setText("User " + userList.get(position).getUserId());
        TextView tv_row_emailAddress = (TextView) vi.findViewById(R.id.tv_row_emailAddress);
        tv_row_emailAddress.setText("Adresse mail : " + userList.get(position).getEmailAddress().replace("'",""));
        TextView tv_row_rights = (TextView) vi.findViewById(R.id.tv_row_rights);
        if(userList.get(position).getRights() == 0) {
            tv_row_rights.setText("Droits d'administration : Oui");
        } else {
            tv_row_rights.setText("Droits d'administration : Non");
        }
        return vi;
    }
}
