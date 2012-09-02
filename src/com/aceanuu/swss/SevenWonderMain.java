package com.aceanuu.swss;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aceanuu.swss.R;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import com.viewpagerindicator.TabPageIndicator;

public class SevenWonderMain extends SherlockActivity  {
    
    ScrollView tabContents;
    int numPlayers;
    LinearLayout mainBody;
    ActionBar ab;
    AwesomePagerAdapter awesomeAdapter;
    ViewPager awesomePager;
    int dp52;
    int dp28;
    String[] tabTitles;
    ArrayList<Integer> medals;
    boolean expanded;
    ArrayList<Integer> winners;
    LayoutInflater factory;
    final int NUM_TABS = 10;
    private Context ctx;
    public PlayerAdapter nameAdapter;
    public ArrayList<ScoreAdapter> scoreAdapterList;
    public SumAdapter sumAdapter;
    public ArrayList<String> playerNames;
    public ArrayList<Integer> sumList; 
    public ArrayList<ArrayList<Integer>> stepWinner; 
    int currentMax;
    
    public void init()
    {
        currentMax = -99999999;
        winners = new ArrayList<Integer>();
        stepWinner  = new ArrayList<ArrayList<Integer>>();
        expanded = true;
        
        ctx = this.getApplicationContext();
        playerNames = new ArrayList<String>();
        
        scoreAdapterList = new ArrayList<ScoreAdapter>();
        for (int i = 0; i < NUM_TABS-2; i++)
        {
            scoreAdapterList.add(new ScoreAdapter(i));
            stepWinner.add(new ArrayList<Integer>());
            
        }
        nameAdapter = new PlayerAdapter();
        sumAdapter = new SumAdapter();
        
        sumList = new ArrayList<Integer>();
        
        awesomeAdapter = new AwesomePagerAdapter();
        awesomePager = (ViewPager) findViewById(R.id.awesomepager);
        awesomePager.setAdapter(awesomeAdapter);

        mainBody = (LinearLayout) findViewById(R.id.main);

        final float scale = getResources().getDisplayMetrics().density;
        dp52 = (int) (52 * scale + 0.5f);
        dp28 = (int) (28 * scale + 0.5f);
        
        numPlayers = 0;
        factory = LayoutInflater.from(this);
        
        tabTitles = this.getResources().getStringArray(R.array.tabs);
        
        medals = new ArrayList<Integer>();
        medals.add(R.drawable.triangle_military);
        medals.add(R.drawable.triangle_money);
        medals.add(R.drawable.triangle_wonder);
        medals.add(R.drawable.triangle_civilian);
        medals.add(R.drawable.triangle_commercial);
        medals.add(R.drawable.triangle_guilds);
        medals.add(R.drawable.triangle_science);
        medals.add(R.drawable.triangle_leader);
        
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_navigation);
        init();

        this.getActionBar().setTitle("Score Sheet");
        
        TabPageIndicator tabInd = (TabPageIndicator) findViewById(R.id.indicatorzulu);
        tabInd.setViewPager(awesomePager);
        
        tabInd.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position == NUM_TABS - 1)
                {
                    sumScore(); 
//                    Log.i("SS", "Sum SCORE: " + position);
                }
            }
        });
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Reset Scores")
            .setNumericShortcut('0')
            .setIcon(R.drawable.ic_refresh)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add("Add Player") 
            .setNumericShortcut('3')
            .setIcon(R.drawable.ic_add)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //This uses the imported MenuItem from ActionBarSherlock
        char sc = item.getNumericShortcut();
        if(sc == '0')
        {       
            Dialog temp =  new AlertDialog.Builder(this)
            .setTitle("Reset Scores?")                
            .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    resetScores();
                 }

             })
             .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {

                     /* User clicked No so do some stuff */
                 }
             })
            .create();
            
            temp.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    //resetScores();
                }
            });
            
            temp.show();
            return true;
        }else
        if(sc == '3')
        {
            addPlayerPrompt();
            return true;
        }
        
        return false;
        
    }
    

    public void resetScores() {
        for(int i = 0; i < playerNames.size(); i++)
        {
         
            for(int step = 0; step < scoreAdapterList.size(); step++)
            {
                scoreAdapterList.get(step).thisStepScoreList.set(i, 0);
            }
            sumList.set(i, 0);
           
        }
        currentMax = -999999999;
        winners.clear();
        sumScore();
        notifyAllAdapters();  
        
    }
    
    private void addPlayerPrompt() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);                 
        alert.setTitle("Player Name");  
        alert.setMessage("Enter Player Name");                

         final EditText input = new EditText(this); 
         input.setInputType(16384);
         input.requestFocus();
         alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) {  
                if(input.getText().toString().length() > 0)
                    addPlayer(input.getText().toString());
                else
                {
                    Toast.makeText(ctx, "Must Enter a Player Name", Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); 

                }           
               }  
             });  

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); 
                }
            });

            AlertDialog alertToShow = alert.create();

            alertToShow.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            alertToShow.show();
    }
    
    public void setWinner()
    {

        winners.clear();
        currentMax = 0;
        
        for(int i = 0; i < playerNames.size(); i ++)
        {
            if(sumList.get(i) == currentMax)
            {
                if(!winners.contains(i))
                    winners.add(i);
            }
            else if(sumList.get(i) > currentMax)
            {
                currentMax = sumList.get(i);
                winners.clear();
                winners.add(i);
            }
        }
        
//        for(int herp : winners)
//        {
////            Log.i("WINNER", "Winner Index: " + herp);
//        }
        
        
    }

    private void addPlayer(String name) {

        playerNames.add(name);
        for(int step = 0; step < scoreAdapterList.size(); step++)
        {
            
            scoreAdapterList.get(step).thisStepScoreList.add(0);
        }
        sumList.add(0);

        winners.clear();
        sumScore();
        notifyAllAdapters();     
    }

    private void removePlayer(int position) {

        playerNames.remove(position);
        for(int step = 0; step < scoreAdapterList.size(); step++)
        {
            
            scoreAdapterList.get(step).thisStepScoreList.remove(position);
        }
        sumList.remove(position);
        notifyAllAdapters();     
    }
    
    
    private void sumScore()
    {
//
//        Log.i("sumScore", "Score Summed");
    //        //OLD STEP SCORING
    //        for(int i = 0; i < playerNames.size(); i++)
    //        {
    //            int tempScore = 0;
    //            
    //            for(int step = 0; step < scoreAdapterList.size(); step++)
    //            {
    //                tempScore += scoreAdapterList.get(step).thisStepScoreList.get(i);
    //            }
    //            
    //            sumList.set(i, tempScore);
    //        }
        
        int temp = 0     ;
        int stepMax      ;

        Log.i("Debug Medals", "BEGIN DEBUG ========================================");
        for(int i = 0; i < NUM_TABS-2; i++)
        {
            stepMax = -9999999;
            Log.i("Debug Medals", tabTitles[i+1] + "-------------------------------");
            for(int p = 0; p < playerNames.size(); p++)
            {

                temp = 0;
                if(i == 0)
                {

//                    Log.i("Debug Medals", "RESET SCORE");
                    sumList.set(p, 0);
                }
                int thisStepScore = scoreAdapterList.get(i).thisStepScoreList.get(p);

//                Log.i("Debug Medals", "Step:" + thisStepScore);
                Log.i("Debug Medals", "Player Name: " + playerNames.get(p) + " = " + thisStepScore);
                
                temp = sumList.get(p) + thisStepScore;
                
                sumList.set(p, temp);
                
                if(stepMax < thisStepScore)
                {
                    stepMax = thisStepScore;
                    stepWinner.get(i).clear();
                    if(thisStepScore != 0)
                    {
                        stepWinner.get(i).add(p);
                        Log.i("Debug Medals", playerNames.get(p) + " WINS");
                    }else
                        Log.i("Debug Medals", playerNames.get(p) + " WINS with ZERO");
                }
                else
                if(stepMax == thisStepScore)
                {

                    if(thisStepScore != 0)
                    {
                        stepWinner.get(i).add(p);
                        Log.i("Debug Medals", playerNames.get(p) + " TIES");
                    }else
                        Log.i("Debug Medals", playerNames.get(p) + " TIES with ZERO");
                    
                }
            } 
            
        }
        
        
        setWinner();
        notifyAllAdapters();     
    }


    public void notifyAllAdapters()
    {
        nameAdapter.notifyDataSetChanged();
        sumAdapter.notifyDataSetChanged();
        for(ScoreAdapter temp : scoreAdapterList) {
            temp.notifyDataSetChanged();
        }
    }
    
    
    public class AwesomePagerAdapter extends PagerAdapter {
        
        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position % tabTitles.length].toUpperCase();
        }

        @Override
        public Object instantiateItem(View collection, int position) {

//            Log.i("PagerDerpin", "Page Position: " + position);
            
            View temp = getLayoutInflater().inflate(R.layout.tabpanel, null);
            ((ViewPager) collection).addView(temp, 0);

              
            if(position == 0)
            {
                ListView content = (ListView) temp.findViewById(R.id.pagerContent);
                content.setAdapter(nameAdapter);
                
                return temp;
            }
            else if(position == 9)
            {
//                Log.i("HOLY HELL", "MAKING SUM ADAPTER @ POSITION: " + position);
                ListView content = (ListView) temp.findViewById(R.id.pagerContent);
                content.setAdapter(sumAdapter);
                return temp;
            }
            else
            {
                ListView content = (ListView) temp.findViewById(R.id.pagerContent);
                content.setAdapter(scoreAdapterList.get(position - 1));
                return temp;
            }
        }

        @Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
        }
        
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==((View)object);
        }

        @Override
        public void finishUpdate(View arg0) {

            int newCur = awesomePager.getCurrentItem();            
        }
        
        @Override
        public void startUpdate(View arg0) {
                                    
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    /* PLAYER ADAPTER HERE ======================================================================================
     * 
     * 
     */
    private class PlayerAdapter extends BaseAdapter {
        
        public PlayerAdapter()
        {
            
        }    
    
        @Override
        public int getCount() {
            return playerNames.size();
        }
    
        @Override
        public Object getItem(int position) {
            return playerNames.get(position);
        }
    
        @Override
        public long getItemId(int position) {
            return 0;
        }
    
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {    
            RelativeLayout tempView  = (RelativeLayout) LayoutInflater.from(ctx).inflate(com.aceanuu.swss.R.layout.playerblock, null);
            TextView playerName = (TextView) tempView.findViewById(R.id.playerName);
            playerName.setText(playerNames.get(position));
            Button remove = (Button) tempView.findViewById(R.id.removePlayer);
            remove.setId(position);
          
            remove.setOnClickListener( new OnClickListener() {
              @Override
              public void onClick(View arg0) {
                  removePlayer(arg0.getId());
                  notifyDataSetChanged();
              }
            });
            return tempView;
        }
    }
    
    private class ScoreAdapter extends BaseAdapter {
        
        int adapterIndex;
        ArrayList<Integer> thisStepScoreList;
        
        public ScoreAdapter(int ind)
        {
            thisStepScoreList = new ArrayList<Integer>();
            adapterIndex = ind;
            playerNames = new ArrayList<String>();
        }
    
    
        @Override
        public int getCount() {
            return playerNames.size();
        }
    
        @Override
        public Object getItem(int position) {
            return playerNames.get(position);
        }
    
        @Override
        public long getItemId(int position) {
            return 0;
        }
    
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {    
            RelativeLayout tempView  = (RelativeLayout) LayoutInflater.from(ctx).inflate(com.aceanuu.swss.R.layout.playerblock, null);
            TextView playerName = (TextView) tempView.findViewById(R.id.playerName);
            playerName.setText(playerNames.get(position));
            
            Button remove = (Button) tempView.findViewById(R.id.removePlayer);
            remove.setVisibility(View.GONE);
            
            RelativeLayout scoreBox = (RelativeLayout) tempView.findViewById(R.id.scoreBlock);
            scoreBox.setVisibility(View.VISIBLE);
            
            
            Button pos = (Button) tempView.findViewById(R.id.pos);
            Button neg = (Button) tempView.findViewById(R.id.neg);
            final EditText score = (EditText) tempView.findViewById(R.id.stepScore);
            score.setText(thisStepScoreList.get(position) + "");
            score.setRawInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
            score.setInputType(InputType.TYPE_CLASS_NUMBER);
            
            final int fpos = position;
            score.addTextChangedListener(new TextWatcher(){
                public void afterTextChanged(Editable s) {
                    if(score.getText().toString().length() == 0)
                        thisStepScoreList.set(fpos, 0);
                    else
                        thisStepScoreList.set(fpos, Integer.parseInt(score.getText().toString()));
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                public void onTextChanged(CharSequence s, int start, int before, int count){}
            }); 
            pos.setOnClickListener( new OnClickListener() {
    
              @Override
              public void onClick(View arg0) {
                  int scoreval = Integer.parseInt(score.getText().toString());
                  scoreval++;
                  score.setText(scoreval + "");
              }
            });
            neg.setOnClickListener( new OnClickListener() {
                
                @Override
                public void onClick(View arg0) {
                    int scoreval = Integer.parseInt(score.getText().toString());
                    scoreval--;
                    score.setText(scoreval + "");
                }
              });
            return tempView;
        }
    }
    
    
    
    private class SumAdapter extends BaseAdapter {
        
        
        public SumAdapter()
        {
        }
    
    
        @Override
        public int getCount() {
            return playerNames.size();
        }
    
        @Override
        public Object getItem(int position) {
            return playerNames.get(position);
        }
    
        @Override
        public long getItemId(int position) {
            return 0;
        }
    
        @Override
        public View getView(int position, View convertView, ViewGroup parent) { 
            RelativeLayout tempView  = (RelativeLayout) LayoutInflater.from(ctx).inflate(com.aceanuu.swss.R.layout.playerblock, null);
            TextView playerName = (TextView) tempView.findViewById(R.id.playerName);
            playerName.setText(playerNames.get(position));
            
            Button remove = (Button) tempView.findViewById(R.id.removePlayer);
            remove.setVisibility(View.GONE);
            
            TextView sumBox = (TextView) tempView.findViewById(R.id.totalScore);
            sumBox.setVisibility(View.VISIBLE);
            sumBox.setText(sumList.get(position)+ "");
            

            LinearLayout medalBox = (LinearLayout) tempView.findViewById(R.id.medalBox);
            
            medalBox.setVisibility(View.VISIBLE);
            
            int i = 0;
            
            for(ArrayList<Integer> playersForThisStep : stepWinner)
            {
                if(playersForThisStep.contains(position))
                {
                    ImageView tempIV = new ImageView(ctx);
                        Bitmap bmp = BitmapFactory.decodeResource(getResources(),medals.get(i));
                        int width = dp28;
                        int height = dp28;
                        Bitmap resizedbitmap = Bitmap.createScaledBitmap(bmp, width, height, true);
                        tempIV.setImageBitmap(resizedbitmap);
                    medalBox.addView(tempIV);
                }
                i++;
            }
            
            
            if(winners.contains(position)) 
            {
                sumBox.setTextColor(Color.GREEN);
            }
            
            return tempView;
        }
        
    }

}