/**
 * Copyright(c) 2014 DRAWNZER.ORG PROJECTS -> ANURAG
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *                             
 *                             anurag.dev1512@gmail.com
 *
 */

package org.anurag.adapters;

import java.util.ArrayList;

import org.anurag.file.quest.Constants;
import org.anurag.file.quest.FileQuestHD;
import org.anurag.file.quest.Item;
import org.anurag.file.quest.OpenFileDialog;
import org.anurag.file.quest.R;
import org.anurag.file.quest.SDAdapter;
import org.anurag.file.quest.SDManager;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.twotoasters.jazzylistview.JazzyHelper;


public class SdCardPanel extends Fragment{
	
	private static ListView list;
	private ArrayList<Item> adapter_list;
	private static LoadList load;
	private static SDManager manager;
	private int counter;
	public static int[] ITEMS;
	private static SDAdapter adapter;
	public SdCardPanel() {
		// TODO Auto-generated constructor stub
		counter = 0;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.list_view_hd, container , false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		list = (ListView)view.findViewById(R.id.list_view_hd);		
		list.setSelector(R.drawable.list_selector_hd);
		setAnim(list);
		if(load == null){
			load = new LoadList();
			load.execute();
		}	
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				
				if(Constants.LONG_CLICK){
					
					if(ITEMS[position] != 1){
						ITEMS[position] = 1;
						arg1.setBackgroundColor(getResources().getColor(R.color.white_grey));
						++counter;
					}else if(ITEMS[position] == 1){
						ITEMS[position] = 0;
						arg1.setBackgroundColor(Color.WHITE);
						if(--counter == 0)
							getActivity().sendBroadcast(new Intent("inflate_normal_menu"));
					}
					
					return;					
				}
				
				Item item = adapter_list.get(position);
				if(item.isDirectory()){
					//selecting a folder....
					manager.pushPath(item.getPath());
					FileQuestHD.notify_Title_Indicator(2, item.getName());
					load.execute();
				}else{
					//selecting a file....
					new OpenFileDialog(getActivity(), Uri.parse(item.getPath())
							, Constants.size.x*8/9);
				}
			}
		});
		
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				boolean sendBroadcast = false;
				if(ITEMS == null){
					ITEMS = new int[adapter_list.size()];
					sendBroadcast = true;
				}
				
				if(ITEMS[arg2] != 1){
					arg1.setBackgroundColor(getResources().getColor(R.color.white_grey));
					ITEMS[arg2] = 1;
					++counter;
				}else if(ITEMS[arg2] == 1){
					ITEMS[arg2] = 0;
					arg1.setBackgroundColor(Color.WHITE);
					if(--counter == 0)
						getActivity().sendBroadcast(new Intent("inflate_normal_menu"));
				}
				
				if(sendBroadcast)
					getActivity().sendBroadcast(new Intent("inflate_long_click_menu"));
				return true;
			}
		});
		
	}
		
	/**
	 * this function sets transition effect for list view.... 
	 * @param list2
	 */
	private void setAnim(ListView list2) {
		// TODO Auto-generated method stub
		JazzyHelper help = new JazzyHelper(getActivity(), null);
		help.setTransitionEffect(Constants.LIST_ANIM);
		list2.setOnScrollListener(help);
	}



	/**
	 * 
	 * @author anurag
	 *
	 */
	private class LoadList extends AsyncTask<Void , Void , Void>{
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
		}		
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			adapter = new SDAdapter(getActivity(), adapter_list);
			list.setAdapter(adapter);
			load = new LoadList();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if(manager == null)
				manager = new SDManager(getActivity());
			adapter_list = manager.getList();
			return null;
		}		
	}	
	
	/**
	 * moves one level back....
	 */
	public static void navigate_to_back(){
		manager.popTopPath();
		FileQuestHD.notify_Title_Indicator(2, manager.getCurrentDirectoryName());
		load.execute();
	}
	
	/**
	 * sets the list view selector as per selected theme
	 * dynamically by user....
	 */
	/*public static void setListSelector(){
		list.setSelector(Constants.SELECTOR_STYLE);
	}*/
	
	/**
	 * 
	 * @return true if current directory is /
	 */
	public static boolean isAtTopLevel(){
		if(manager.getCurrentDirectory().equalsIgnoreCase(Constants.PATH))
			return true;
		return false;
	}
	

	/**
	 * reloads the current folder
	 * called when the folder icon has to be changed....
	 */
	public static void notifyDataSetChanged(){
		load.execute();
	}
	
	/**
	 * refreshes the list view....
	 */
	public static void refresh_list(){
		list.setAdapter(null);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		load.execute();
	}
	
	/**
	 * this function clears the selected items via long click from lits view....
	 */
	public static void clear_selected_items(){
		list.setAdapter(adapter);				
	}
}
