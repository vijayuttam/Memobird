package com.czm.xcricheditor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.czm.xcricheditor.editor.EditItem;
import com.czm.xcricheditor.R;
import com.czm.xcricheditor.helper.ItemTouchHelperViewHolder;
import com.czm.xcricheditor.helper.OnStartDragListener;
import com.czm.xcricheditor.listener.ComponentAdapterListener;
import com.czm.xcricheditor.view.ImageDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.*;

public class XCRichEditorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
    ItemTouchHelperAdapter {
    private final LayoutInflater mLayoutInflater;
    private List<EditItem> mData;
    private Context mContext;
    private ComponentAdapterListener mListener;
    private int mImageheight;
    private final OnStartDragListener mDragStartListener;

    public XCRichEditorAdapter(Context context, OnStartDragListener dragStartListener) {
        super();
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mImageheight = 1000;
        mDragStartListener = dragStartListener;
    }

    public List<EditItem> getData() {
        return mData;
    }

    public void setData(List<EditItem> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == ITEM_TYPE.ITEM_TYPE_IMAGE.ordinal()){
            View view = mLayoutInflater.inflate(R.layout.item_image_component, viewGroup, false);
            ImageViewHolder holder = new ImageViewHolder(view);
            return holder;
        }else{
            View view = mLayoutInflater.inflate(R.layout.item_text_component, viewGroup, false);
            TextViewHolder holder = new TextViewHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof TextViewHolder) {
            ((TextViewHolder) holder).bindData(mData.get(position).getContent());
        } else if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).bindData(mData.get(position).getUri());
            ((ImageViewHolder) holder).img.setOnLongClickListener(new OnLongClickListener() {
                @Override public boolean onLongClick(View view) {
                    mDragStartListener.onStartDrag(holder);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (mData.get(position).getType()) {
            case 1:
                return ITEM_TYPE.ITEM_TYPE_IMAGE.ordinal();
            case 2 :
                return ITEM_TYPE.ITEM_TYPE_VIDEO.ordinal();
            default:
                return ITEM_TYPE.ITEM_TYPE_TEXT.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        if (null != mData && mData.size() > 0) {
            return mData.size();
        } else {
            return 0;
        }
    }

    public void setComponentAdapterListener(ComponentAdapterListener listener) {
        mListener = listener;
    }

    @Override public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mData, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mData, i, i - 1);
            }
        }
        notifyDataSetChanged();
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override public void onItemDismiss(int position) {
        mData.remove(position);
        notifyDataSetChanged();
        notifyItemRemoved(position);
    }

    public enum ITEM_TYPE {
        ITEM_TYPE_TEXT,
        ITEM_TYPE_IMAGE,
        ITEM_TYPE_VIDEO
    }


    public class TextViewHolder extends RecyclerView.ViewHolder {

        private EditText editText;
        private OnKeyListener mKeyListener;
        private OnFocusChangeListener mFocusChangeListener;
        private SparseArray<String> mTextArray;

        public TextViewHolder(View itemView) {
            super(itemView);
            editText = (EditText) itemView.findViewById(R.id.id_item_text_component);
            editText.setOnKeyListener(mKeyListener);
            editText.setOnFocusChangeListener(mFocusChangeListener);
            mTextArray = new SparseArray<>();
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String content = editText.getText().toString().trim();
                    mData.get(getAdapterPosition()).setContent(content);
                    /*
                    Matcher matcher = Pattern.compile("\n").matcher(content);
                    while (matcher.find()) {
                        mTextArray.append(content.indexOf("\n"), matcher.group(1));
                        content = content.replaceFirst("\n", "");
                    }
                    if (mTextArray.size() == 0){
                        mData.get(getAdapterPosition()).setContent(content);
                    } else {
                        for (int i = 0; i < mTextArray.size(); i++) {
                            mData.get(getAdapterPosition() + i).setContent(mTextArray.get(i));
                        }
                    }
                    */
                }
            });

            mKeyListener = new OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                        EditText editText = (EditText) v;
                        //onBackspacePress(editText);
                    }
                    if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    }
                    return false;
                }
            };

            mFocusChangeListener = new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        //mLastFocusEdit = (EditText) v;
                    }
                }
            };
        }

        public void bindData(String content) {
            editText.setText(content);
            int pos = getAdapterPosition();
            if(pos == 0 && editText.length() <= 0){
                editText.setHint("\n" + "Please enter the content");
            }else{
                editText.setHint("");
            }
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        ImageDraweeView img;
        Button deleteBtn;

        public ImageViewHolder(View itemView) {
            super(itemView);
            img = (ImageDraweeView) itemView.findViewById(R.id.id_item_image_component);
            deleteBtn = (Button) itemView.findViewById(R.id.delete_btn);
            deleteBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.delete(getAdapterPosition());
                    }
                }
            });
        }

        public void bindData(Uri uri) {
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(210, 210))
                    .setAutoRotateEnabled(true)
                    .build();
            //img.setUri(imageRequest);
            img.setUri(imageRequest);

            mImageheight = img.getMeasuredHeight();
        }

        @Override public void onItemSelected() {
            img.getLayoutParams().height = 200;
            img.setBackgroundColor(Color.LTGRAY);
        }

        @Override public void onItemClear() {
            img.getLayoutParams().height = mImageheight;
            img.setBackgroundColor(0);
        }
    }

}
