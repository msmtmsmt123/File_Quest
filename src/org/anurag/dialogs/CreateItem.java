package org.anurag.dialogs;

import java.io.File;
import java.io.IOException;

import org.anurag.file.quest.Constants;
import org.anurag.file.quest.Item;
import org.anurag.file.quest.R;
import org.anurag.fragments.RootPanel;
import org.anurag.fragments.SdCardPanel;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * this class inflates a dialog and asks for name for new item to be created
 * and creates the item(folder or empty file)....
 * @author anurag
 *
 */
public class CreateItem {
	
	private Dialog dialog;
	private Button btn;
	private EditText getName;
	
	/**
	 * 
	 * @param ctx
	 * @param currentDir the folder in which the item has to be created....
	 * @param isDir true then create folder....
	 */
	public CreateItem(final Context ctx ,final Item currentDir ,final boolean isDir
			 , final int panel) {
		// TODO Auto-generated constructor stub
		dialog = new Dialog(ctx, Constants.DIALOG_STYLE);
		dialog.setContentView(R.layout.create_new_item_hd);
		dialog.setCancelable(true);
		dialog.getWindow().getAttributes().width = Constants.size.x*8/9;
		dialog.show();
		
		getName = (EditText) dialog.findViewById(R.id.folder_nam);
		
		if(!isDir){
			getName.setHint(R.string.file_name);
			TextView head = (TextView) dialog.findViewById(R.id.head);
			head.setText(R.string.file_name);
		}
		
		btn = (Button) dialog.findViewById(R.id.create);
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				File file = currentDir.getFile();
				file = new File(file, getName.getText().toString());
				if(isDir)
					file.mkdirs();
				else
					try {
						file.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				if(file.exists()){
										
					if(panel == 1){
						RootPanel.notifyDataSetChanged();
					}else if(panel == 2){
						SdCardPanel.notifyDataSetChanged();
					}
					
					Toast.makeText(ctx, R.string.itm_created, Toast.LENGTH_SHORT).show();
				}	
				else
					Toast.makeText(ctx, R.string.failed_to_create, Toast.LENGTH_SHORT).show();
				
				dialog.dismiss();
			}
		});
		
	}

}
