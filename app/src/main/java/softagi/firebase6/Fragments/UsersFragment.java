package softagi.firebase6.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import softagi.firebase6.MainActivity;
import softagi.firebase6.R;
import softagi.firebase6.UserModel;

public class UsersFragment extends Fragment
{
    private String status;
    private View view;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<UserModel> list;
    private Button logout;
    private static UserModel uu;

    public UsersFragment(String status)
    {
        this.status = status;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.user_fragment, container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        
        initViews();
        initFirebase();
        getMyData(getUid());

        switch (status)
        {
            case "users":
                getUsers();
                break;
            case "friends":
                getFriends();
                break;
            case "requests":
                getRequests(getUid());
                break;
            default:
                getUsers();
        }
    }

    private void getRequests(String id)
    {
        databaseReference.child("RecieveRequests").child(id).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                list.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    UserModel userModel = dataSnapshot1.getValue(UserModel.class);
                    list.add(userModel);
                }

                usersAdapter adapter = new usersAdapter(list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void getFriends()
    {

    }

    private void getUsers()
    {
        databaseReference.child("Users").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                list.clear();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    UserModel userModel = dataSnapshot1.getValue(UserModel.class);

                    if (!userModel.getId().equals(getUid()))
                    {
                        list.add(userModel);
                    }
                }

                usersAdapter adapter = new usersAdapter(list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void initFirebase()
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void initViews()
    {
        recyclerView = view.findViewById(R.id.recyclerview);
        logout = view.findViewById(R.id.logout);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<>();

        logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });
    }

    class usersAdapter extends RecyclerView.Adapter<usersAdapter.usersVH>
    {
        List<UserModel> models;

        usersAdapter(List<UserModel> models)
        {
            this.models = models;
        }

        @NonNull
        @Override
        public usersVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent,false);
            return new usersVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull usersVH holder, int position)
        {
            final UserModel userModel = models.get(position);
            String name = userModel.getUsername();
            String img = userModel.getPhoto();
            String mob = userModel.getMobilr();
            final String id = userModel.getId();

            //holder.status(id,models,position);

            holder.name.setText(name);
            holder.mobile.setText(mob);

            Picasso.get()
                    .load(img)
                    .into(holder.circleImageView);

            switch (status)
            {
                case "users":
                    holder.button.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            databaseReference.child("RecieveRequests").child(id).child(getUid()).setValue(uu);
                            databaseReference.child("SentRequests").child(getUid()).child(id).setValue(userModel);
                            Toast.makeText(getContext(), "Sent", Toast.LENGTH_SHORT).show();
                        }
                    });

                    holder.setRequestsStatus(id);
                    break;
                case "friends":

                    break;
                case "requests":
                    holder.button.setText("accept / decline");
                    break;
            }
        }

        @Override
        public int getItemCount()
        {
            return models.size();
        }

        class usersVH extends RecyclerView.ViewHolder
        {
            TextView name,mobile;
            CircleImageView circleImageView;
            Button button;

            usersVH(@NonNull View itemView)
            {
                super(itemView);

                name = itemView.findViewById(R.id.user_name);
                mobile = itemView.findViewById(R.id.user_mobile);
                circleImageView = itemView.findViewById(R.id.user_img);
                button = itemView.findViewById(R.id.user_btn);
            }

            void setRequestsStatus(final String id)
            {
                databaseReference.child("SentRequests").child(getUid()).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild(id))
                        {
                            button.setText("pending request");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
            }
        }
    }

    private void getMyData(String id)
    {
        databaseReference.child("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                uu = userModel;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private String getUid()
    {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}