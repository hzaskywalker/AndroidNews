package com.ihandy.a2014011345.news;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by hza on 16/9/4.
 */

class MenuList{
    LinearLayout layout;

    LinearLayout favor, category_manager, aboutme;
    MenuList(final MainActivity father){
        layout = (LinearLayout)father.findViewById(R.id.menu_list);
        favor = (LinearLayout)father.findViewById(R.id.favorites);
        category_manager = (LinearLayout)father.findViewById(R.id.category_manager);
        aboutme = (LinearLayout)father.findViewById(R.id.aboutme);

        aboutme.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("haha", "jump to about me");
                        Intent intent = new Intent(view.getContext(), AboutMe.class);
                        father.startActivity(intent);
                    }
                }
        );

        category_manager.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        father.dump_to_dataset();
                        Log.e("haha", "jump to category manager");
                        Intent intent = new Intent(view.getContext(), CategoryManager.class);
                        father.startActivityForResult(intent, 1);
                    }
                }
        );

        favor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), Favorite.class);
                        father.startActivity(intent);
                    }
                }
        );

        layout.setOnClickListener(new View.OnClickListener(){public void onClick(View view){}});
    }
}

public class MenuLeft{
    Button button;

    DrawerLayout drawerLayout;
    MenuList menuList;
    private MainActivity father;
    private ActionBarDrawerToggle toggle;

    public View findViewById(int id){
        return father.findViewById(id);
    }

    public void setHeader(){
        button = (Button)findViewById(R.id.left_menu_button);
        button.setOnClickListener(
            new View.OnClickListener(){
                public void onClick(View view) {
                    toggleLeftSliding();
                }
            }
        );
    }

    public void setMenuList(){
        menuList=new MenuList(father);
    }

    MenuLeft(MainActivity FatherActivity){
        father = FatherActivity;
        setHeader();
        setMenuList();
        drawerLayout = (DrawerLayout) findViewById(R.id.menu_left_layout);
        drawerLayout.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

//        drawerLayout.setScrimColor(Color.TRANSPARENT);
        toggle=new ActionBarDrawerToggle(
                FatherActivity,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close){
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

        };
        drawerLayout.setDrawerListener(toggle);
    }

    public void toggleLeftSliding(){//该方法控制左侧边栏的显示和隐藏
        Log.i("MenuLeft", "clicked");
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
