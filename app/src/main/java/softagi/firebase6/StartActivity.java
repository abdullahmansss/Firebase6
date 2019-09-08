package softagi.firebase6;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import softagi.firebase6.Fragments.UsersFragment;


public class StartActivity extends AppCompatActivity
{
    TabLayout tabLayout;
    ViewPager viewPager;
    FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
        {
            Fragment [] fragments = new Fragment[]
                    {
                            new UsersFragment("users"),
                            new UsersFragment("friends"),
                            new UsersFragment("requests")
                    };

            String [] names = new String[]
                    {
                            "USERS",
                            "FRIENDS",
                            "REQUESTS"
                    };

            @Override
            public Fragment getItem(int position)
            {
                return fragments[position];
            }

            @Override
            public int getCount()
            {
                return fragments.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position)
            {
                return names[position];
            }
        };

        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed()
    {
        finishAffinity();
    }
}
