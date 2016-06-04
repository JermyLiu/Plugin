
package cn.com.tiantong.game72.push;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class ArrayListAdapter<T> extends BaseAdapter{
	
	protected ArrayList<T> mList;
	protected Context mContext;
	protected ListView mListView;
	static int i = 0;
	
	public ArrayListAdapter(Context context){
		this.mContext = context;
	}

	
	public int getCount() {
		
		return mList==null ? 0:mList.size();
	}

	
	
	public Object getItem(int position) {
		if(position >= getCount())
			return null;
		return mList == null ? null : mList.get(position);
	}

	
	public long getItemId(int position) {
		return position;
	}

	public void removeItem(int position){
		mList.remove(position);
		this.notifyDataSetChanged();
	}
	
	abstract public View getView(int position, View convertView, ViewGroup parent);
	
	public void setLists(ArrayList<T> list){
		if(this.mList == null){
			this.mList = list;
		}else{
			this.mList.addAll(list);

		}
		notifyDataSetChanged();
	}
	public void setList(ArrayList<T> list){
		this.mList = list;
		notifyDataSetChanged();
	}
	
	public ArrayList<T> getList(){
		return mList;
	}
	
	public void clear(){
		if(mList!=null)
			mList.clear();
		notifyDataSetChanged();
	}
	
	@SuppressWarnings("unchecked")
	public void setList(T[] list){
		ArrayList<T> arrayList = new ArrayList<T>(list.length);  
		for (T t : list) {  
			arrayList.add(t);  
		}  
		setList(arrayList);
	}
	
	public ListView getListView(){
		return mListView;
	}
	
	public void setListView(ListView listView){
		mListView = listView;
	}

}
