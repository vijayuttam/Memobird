package com.czm.xcricheditor.editor;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.czm.xcricheditor.R;
import com.czm.xcricheditor.adapter.XCRichEditorAdapter;
import com.czm.xcricheditor.helper.OnStartDragListener;
import com.czm.xcricheditor.helper.SimpleItemTouchHelperCallback;
import com.czm.xcricheditor.listener.ComponentAdapterListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.czm.xcricheditor.util.EditorUtilsKt.IMAGE_SRC_REGEX;
import static com.czm.xcricheditor.util.EditorUtilsKt.px2dip;

public class XCRichEditor extends RelativeLayout implements OnStartDragListener {

    private View mRoot;
    private Context mContext;
    private List<EditItem> mDatas;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private XCRichEditorAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private int lastPosition = 0;
    private EditText lastEditText;
    private SparseArray<String> mImageArray;

    public XCRichEditor(Context context) {
        this(context, null);
    }
    public XCRichEditor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public XCRichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        mImageArray = new SparseArray<>();
        mRoot = View.inflate(mContext, R.layout.layout_rich_editor, this);
        mRecyclerView = (RecyclerView) mRoot.findViewById(R.id.id_edit_component);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(px2dip(mContext, 10)));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mAdapter = new XCRichEditorAdapter(mContext, this);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mAdapter.setComponentAdapterListener(new ComponentAdapterListener() {
            @Override
            public void change(int position, EditText editText) {
               lastEditText = editText;
               lastPosition = position;
            }

            @Override public void enter(int position, EditText editText) {
                lastEditText = editText;
                lastPosition = position;
                if (lastPosition >= 0 && null != lastEditText && !TextUtils.isEmpty(lastEditText.getText().toString().trim())) {
                    String lastEditStr = lastEditText.getText().toString();
                    int cursorIndex = lastEditText.getSelectionStart();
                    String editStr1 = lastEditStr.substring(0, cursorIndex).trim();
                    String editStr2 = lastEditStr.substring(cursorIndex).trim();
                    mDatas.get(lastPosition).setContent(editStr1);
                    //if (mDatas.size() < 0) {
                    //    for (int i=0; i < mDatas.size(); i++) {
                    //        mDatas.get(i).setContent(mDatas.get(i).getContent());
                    //    }
                    //}
                    if (!TextUtils.isEmpty(editStr2)) {
                        EditItem postData = new EditItem(0, editStr2, null);
                        mDatas.add(++lastPosition, postData);
                    } else {
                        EditItem postData = new EditItem(0, "", null);
                        mDatas.add(++lastPosition, postData);
                        mAdapter.notifyItemInserted(lastPosition);
                    }
                    mAdapter.modifiedData(mDatas);
                }
            }

            @Override
            public void delete(int position) {
                if(position == mAdapter.getItemCount() - 1){
                    //the last one
                    mDatas.remove(position);
                    mAdapter.notifyDataSetChanged();
                }else if(position > 0){
                    String str = "";
                    if (position > 0 && mDatas.get(position - 1).getType() == 0) {
                        str = mDatas.get(position - 1).getContent();
                    }
                    if (position < mDatas.size() - 1 && mDatas.get(position + 1).getType() == 0) {
                        str += mDatas.get(position + 1).getContent();
                    }
                    mDatas.remove(position + 1);
                    mDatas.remove(position);
                    mDatas.remove(position - 1);
                    EditItem textData = new EditItem(0, str, null);
                    mDatas.add(position - 1, textData);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        if (null == mDatas)
            mDatas = new ArrayList<>();

        EditItem data = new EditItem(0, "", null);
        mDatas.add(lastPosition, data);
        mAdapter.setData(mDatas);
    }


    public String getRichText(){
        String content = "";
        if (null != mDatas && mDatas.size() > 0) {
            for (EditItem item : mDatas) {
                if (item.getType() == 0) {
                    content += item.getContent();
                } else if (item.getType() == 1) {
                    String pathTag = "<img src=\"" + item.getContent() + "\"/>";
                    content += pathTag;
                }
            }
        }
        return content;
    }

    public void addImages(List<EditItem> info) {
        addData(info);
    }

    public void addImage(EditItem item){
        List<EditItem> datas = new ArrayList<>();
        datas.add(item);
        addData(datas);
    }

    public void setRichText(String mContent) {

        if (mDatas != null && mDatas.size() > 0) {
            mDatas.clear();
        }

        if (mContent == null){
            mContent = "";

        }

        Matcher m = Pattern.compile(IMAGE_SRC_REGEX).matcher(mContent);
        while (m.find()) {
            mImageArray.append(mContent.indexOf("<img"), m.group(1));
            mContent = mContent.replaceFirst("<img[^>]*>", "");
        }

        if (mImageArray.size() == 0) {
            EditItem textItem = new EditItem(0, mContent, null);
            mDatas.add(textItem);
        } else {
            for (int i = 0; i < mImageArray.size(); i++) {
                String s;
                if (i == 0 && (mImageArray.size() - 1 == 0)) {
                    s = mContent.substring(0, mImageArray.keyAt(i));
                    EditItem textItem1 = new EditItem(0, s, null);
                    mDatas.add(textItem1);
                    EditItem imgItem = new EditItem(1, mImageArray.valueAt(i), Uri.fromFile(new File(mImageArray.valueAt(i))));
                    mDatas.add(imgItem);
                    s = mContent.substring(mImageArray.keyAt(i), mContent.length());
                    EditItem textItem2 = new EditItem(0, s, null);
                    mDatas.add(textItem2);
                } else if (i == 0) {
                    s = mContent.substring(0, mImageArray.keyAt(i));
                    EditItem textItem = new EditItem(0, s, null);
                    mDatas.add(textItem);
                    EditItem imgItem = new EditItem(1, mImageArray.valueAt(i), Uri.fromFile(new File(mImageArray.valueAt(i))));
                    mDatas.add(imgItem);
                } else if (i == mImageArray.size() - 1) {
                    s = mContent.substring(mImageArray.keyAt(i - 1), mImageArray.keyAt(i));
                    EditItem textItem1 = new EditItem(0, s, null);
                    mDatas.add(textItem1);
                    s = mContent.substring(mImageArray.keyAt(i), mContent.length());
                    EditItem imgItem = new EditItem(1, mImageArray.valueAt(i), Uri.fromFile(new File(mImageArray.valueAt(i))));
                    mDatas.add(imgItem);
                    EditItem textItem2 = new EditItem(0, s, null);
                    mDatas.add(textItem2);
                } else {
                    s = mContent.substring(mImageArray.keyAt(i - 1), mImageArray.keyAt(i));
                    EditItem textItem = new EditItem(0, s, null);
                    mDatas.add(textItem);
                    EditItem imgItem = new EditItem(1, mImageArray.valueAt(i), Uri.fromFile(new File(mImageArray.valueAt(i))));
                    mDatas.add(imgItem);
                }
            }
        }

        mAdapter.setData(mDatas);
    }


    private void addData(List<EditItem> info) {
        if (lastPosition >= 0 && null != lastEditText && !TextUtils.isEmpty(lastEditText.getText().toString().trim())) {
            String lastEditStr = lastEditText.getText().toString();
            int cursorIndex = lastEditText.getSelectionStart();
            String editStr1 = lastEditStr.substring(0, cursorIndex).trim();
            String editStr2 = lastEditStr.substring(cursorIndex).trim();
            boolean ispre = false;
            boolean ispost = false;
            if (lastPosition > 0 && mDatas.get((lastPosition - 1)).getType() == 0) { //pre item is text
                editStr1 = mDatas.get((lastPosition - 1)).getContent() + editStr1;
                ispre = true;
            }
            if (lastPosition < mDatas.size() - 1 && mDatas.get((lastPosition + 1)).getType() == 0) { //post item is text
                editStr2 = editStr2 + mDatas.get((lastPosition + 1)).getContent();
                ispost = true;
            }
            int tmpPosition = lastPosition;
            if (ispre && ispost) {
                mDatas.remove(lastPosition + 1);
                //mDatas.remove(lastPosition);
                //mDatas.remove(lastPosition - 1);
                ++tmpPosition;
            } else if (ispre) {
                mDatas.remove(lastPosition);
                mDatas.remove(lastPosition - 1);
                --tmpPosition;
            } else if (ispost) {
                mDatas.remove(lastPosition + 1);
                mDatas.remove(lastPosition);
            } else {
                mDatas.remove(lastPosition);
            }
            //EditItem preData = new EditItem(0, editStr1, null);
            //mDatas.add(tmpPosition++, preData);
            for (int i = 0; i < info.size(); i++) {
                mDatas.add(tmpPosition++, info.get(i));
                if (i == info.size() - 1 && !TextUtils.isEmpty(editStr2)) {
                    EditItem postData = new EditItem(0, editStr2, null);
                    mDatas.add(tmpPosition++, postData);
                } else {
                    EditItem postData = new EditItem(0, "", null);
                    mDatas.add(tmpPosition++, postData);
                }
            }
        } else {
            for (int i = 0; i < info.size(); i++) {
                mDatas.add(info.get(i));
                EditItem textData = new EditItem(0, "", null);
                mDatas.add(textData);
            }
        }
        mAdapter.setData(mDatas);

    }

    @Override public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
