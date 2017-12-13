package com.czm.xcricheditor.editor;

import android.animation.LayoutTransition;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import com.czm.xcricheditor.R;
import com.czm.xcricheditor.view.ImageDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.czm.xcricheditor.util.EditorUtilsKt.IMAGE_SRC_REGEX;
import static com.czm.xcricheditor.util.EditorUtilsKt.dip2px;

public class SmartEditor extends ScrollView implements View.OnDragListener {

  private static final int EDIT_FIRST_PADDING_TOP = 1;
  private int mImageheight = 0;
  private int disappearingImageIndex = 0;
  private int editNormalPadding = 0;
  private int viewTagIndex = 1;
  private SparseArray<String> mImageArray;
  private String TAG = getClass().getSimpleName();
  private OnKeyListener mKeyListener;
  private OnClickListener mCloseBtnListener;
  private OnFocusChangeListener mFocusChangeListener;
  private OnLongClickListener mOnLongClickListener;
  private LayoutInflater mInflater;
  private LinearLayout mContainerLayout;
  private EditText mLastFocusEdit;
  private LayoutTransition mTransition;
  private List<EditItem> mDatas;

  public SmartEditor(Context context) {
    super(context);
    init(context);
  }

  public SmartEditor(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public SmartEditor(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context mContext){
    mInflater =  LayoutInflater.from(mContext);

    mContainerLayout = new LinearLayout(mContext);
    mContainerLayout.setOrientation(LinearLayout.VERTICAL);
    mContainerLayout.setBackgroundColor(Color.WHITE);
    LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    setupLayoutTransitions(mContainerLayout);
    addView(mContainerLayout, layoutParams);
    mImageArray = new SparseArray<>();

    mKeyListener = new OnKeyListener() {

      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
          EditText editText = (EditText) v;
          onBackspacePress(editText);
        }
        return false;
      }
    };

    mCloseBtnListener = new OnClickListener() {

      @Override
      public void onClick(View v) {
        RelativeLayout parentView = (RelativeLayout) v.getParent();
        onImageCloseClick(parentView);
      }
    };

    mFocusChangeListener = new OnFocusChangeListener() {

      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          mLastFocusEdit = (EditText) v;
        }
      }
    };

    mOnLongClickListener = new OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        ClipData clip = ClipData.newPlainText("", "");
        View.DragShadowBuilder shadow = new View.DragShadowBuilder(v);
        v.startDrag(clip,shadow, v,0);
        return false;
      }
    };

    LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    editNormalPadding = dip2px(mContext, 0);
    EditText firstEdit = createEditText("Please enter the content", dip2px(mContext, EDIT_FIRST_PADDING_TOP));
    mContainerLayout.addView(firstEdit, firstEditParam);
    mLastFocusEdit = firstEdit;
  }

  private EditText createEditText(String hint, int paddingTop) {
    EditText editText = (EditText) mInflater.inflate(R.layout.item_edit_component, null);
    editText.setOnKeyListener(mKeyListener);
    editText.setTag(viewTagIndex++);
    editText.setPadding(editNormalPadding, paddingTop, editNormalPadding, 0);
    editText.setHint(hint);
    editText.setOnFocusChangeListener(mFocusChangeListener);
    return editText;
  }


  private void setupLayoutTransitions(LinearLayout layout) {
    mTransition = new LayoutTransition();
    layout.setLayoutTransition(mTransition);
    mTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
      @Override
      public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
      }

      @Override
      public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
        if (!transition.isRunning() && transitionType == LayoutTransition.CHANGE_DISAPPEARING) {
          mergeEditText();
        }
      }
    });
    mTransition.setDuration(300);
    mContainerLayout.setLayoutTransition(mTransition);
  }

  private void mergeEditText() {

    View preView = mContainerLayout.getChildAt(disappearingImageIndex -1);
    View nextView = mContainerLayout.getChildAt(disappearingImageIndex);

    if (preView != null && preView instanceof EditText && nextView != null && nextView instanceof EditText) {
      EditText preEdit = (EditText) preView;
      EditText nextEdit = (EditText) nextView;
      String str1 = preEdit.getText().toString();
      String str2 = preEdit.getText().toString();
      String mergeText = "";
      if (str2.length() > 0) {
        mergeText = str1 + "\n" + str2;
      } else {
        mergeText = str1;
      }

      mContainerLayout.setLayoutTransition(null);
      mContainerLayout.removeView(nextEdit);
      preEdit.setText(mergeText);
      preEdit.requestFocus();
      preEdit.setSelection(str1.length(), str1.length());
      mContainerLayout.setLayoutTransition(mTransition);
    }
  }

  public void setRichText(String mContent) {

    if (mContent == null){
      mContent = "";

    }

    Matcher m = Pattern.compile(IMAGE_SRC_REGEX).matcher(mContent);
    while (m.find()) {
      mImageArray.append(mContent.indexOf("<img"), m.group(1));
      mContent = mContent.replaceFirst("<img[^>]*>", "");
    }

    if (mImageArray.size() == 0) {
      addEditTextAtIndex(0, mContent);
    } else {
      for (int i = 0; i < mImageArray.size(); i++) {
        String s;
        if (i == 0 && (mImageArray.size() - 1 == 0)) {
          s = mContent.substring(0, mImageArray.keyAt(i));
          addEditTextAtIndex(i, s);
          addImageViewAtIndex(i, mImageArray.valueAt(i), Uri.fromFile(new File(mImageArray.valueAt(i))));
          s = mContent.substring(mImageArray.keyAt(i), mContent.length());
          addEditTextAtIndex(i, s);
        } else if (i == 0) {
          s = mContent.substring(0, mImageArray.keyAt(i));
          addEditTextAtIndex(i, s);
          addImageViewAtIndex(i, mImageArray.valueAt(i), Uri.fromFile(new File(mImageArray.valueAt(i))));
        } else if (i == mImageArray.size() - 1) {
          s = mContent.substring(mImageArray.keyAt(i - 1), mImageArray.keyAt(i));
          addEditTextAtIndex(i, s);
          s = mContent.substring(mImageArray.keyAt(i), mContent.length());
          addImageViewAtIndex(i, mImageArray.valueAt(i), Uri.fromFile(new File(mImageArray.valueAt(i))));
          addEditTextAtIndex(i, s);
        } else {
          s = mContent.substring(mImageArray.keyAt(i - 1), mImageArray.keyAt(i));
          addEditTextAtIndex(i, s);
          addImageViewAtIndex(i, mImageArray.valueAt(i), Uri.fromFile(new File(mImageArray.valueAt(i))));
        }
      }
    }

  }

  public String getRichText(){
    mDatas = buildEditData();
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

  public List<EditItem> buildEditData() {
    List<EditItem> dataList = new ArrayList<>();
    int num = mContainerLayout.getChildCount();
    for (int index = 0; index < num; index++) {
      View itemView = mContainerLayout.getChildAt(index);
      if (itemView instanceof EditText) {
        EditText item = (EditText) itemView;
        EditItem textItem = new EditItem(0, item.getText().toString(), null);
        dataList.add(textItem);
      } else if (itemView instanceof RelativeLayout) {
        ImageDraweeView item = (ImageDraweeView) itemView.findViewById(R.id.id_item_image_component);
        EditItem imageItem = new EditItem(1, item.getUri().getPath(), item.getUri());
        dataList.add(imageItem);
      }
    }

    return dataList;
  }


  private void addEditTextAtIndex(final int index, String editStr) {
    EditText editText2 = createEditText("", 10);
    editText2.setText(editStr);

    mContainerLayout.setLayoutTransition(null);
    mContainerLayout.addView(editText2, index);
    mContainerLayout.setLayoutTransition(mTransition);
  }


  public void addImage(String imagePath, Uri uri) {
    String lastEditStr = mLastFocusEdit.getText().toString();
    int cursorIndex = mLastFocusEdit.getSelectionStart();
    String editStr1 = lastEditStr.substring(0, cursorIndex).trim();
    int lastEditIndex = mContainerLayout.indexOfChild(mLastFocusEdit);

    if (lastEditStr.length() == 0 || editStr1.length() == 0) {
      addImageViewAtIndex(lastEditIndex, imagePath, uri);
    } else {

      mLastFocusEdit.setText(editStr1);
      String editStr2 = lastEditStr.substring(cursorIndex).trim();
      if (mContainerLayout.getChildCount() -1 == lastEditIndex || editStr2.length() > 0) {
        addEditTextAtIndex(lastEditIndex + 1,  editStr2);
      }

      addImageViewAtIndex(lastEditIndex + 1, imagePath, uri);
      mLastFocusEdit.requestFocus();
      mLastFocusEdit.setSelection(editStr1.length(), editStr1.length());
    }
    hideKeyBoard();
  }

  private void addImageViewAtIndex(final int index, String imagePath, Uri uri) {
    final RelativeLayout layout = (RelativeLayout) mInflater.inflate(R.layout.item_image_component, null);
    layout.setTag(viewTagIndex++);
    final Button closeView = (Button)layout.findViewById(R.id.delete_btn);
    ImageDraweeView img = (ImageDraweeView)layout.findViewById(R.id.id_item_image_component);
    img.setTag(imagePath);
    closeView.setOnClickListener(mCloseBtnListener);
    layout.setOnLongClickListener(mOnLongClickListener);
    ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
        .setResizeOptions(new ResizeOptions(210, 210))
        .setAutoRotateEnabled(false)
        .build();
    img.setUri(imageRequest);
    layout.setOnDragListener(this);

    mImageheight = img.getMeasuredHeight();

    mContainerLayout.postDelayed(new Runnable() {
      @Override
      public void run() {
        mContainerLayout.addView(layout, index);
        mContainerLayout.setLayoutTransition(mTransition);
      }
    }, 200);

  }

  private void onImageCloseClick(View view) {
    if (mTransition != null && !mTransition.isRunning()) {
      disappearingImageIndex = mContainerLayout.indexOfChild(view);
      mContainerLayout.removeView(view);
    }
  }

  public void hideKeyBoard() {
    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromInputMethod(mLastFocusEdit.getWindowToken(), 0);
  }

  private void onBackspacePress(EditText editText) {
    int startSelection = editText.getSelectionStart();
    if (startSelection == 0) {
      int editIndex = mContainerLayout.indexOfChild(editText);
      View preView = mContainerLayout.getChildAt(editIndex - 1);
      if (preView != null) {
        if (preView instanceof RelativeLayout) {
          onImageCloseClick(preView);
        } else if (preView instanceof EditText) {
          String str1 = editText.getText().toString();
          EditText preEdit = (EditText) preView;
          String str2 = preEdit.getText().toString();

          mContainerLayout.setLayoutTransition(null);
          mContainerLayout.removeView(editText);
          mContainerLayout.setLayoutTransition(mTransition);

          preEdit.setText(str2 + str1);
          preEdit.requestFocus();
          preEdit.setSelection(str2.length(), str2.length());
          mLastFocusEdit = preEdit;
        }
      }
    }
  }

  @Override public boolean onDrag(View v, DragEvent event) {
    // Defines a variable to store the action type for the incoming
    // event
    float X,Y;
    final int action = event.getAction();
    // Handles each of the expected events
    switch (action) {
      case DragEvent.ACTION_DRAG_STARTED:
        // Invalidate the view to force a redraw in the new tint
        v.invalidate();
        // returns true to indicate that the View can accept the
        // dragged data.
        return true;
      case DragEvent.ACTION_DRAG_ENTERED:
        // Invalidate the view to force a redraw in the new tint
        v.invalidate();
        return false;
      case DragEvent.ACTION_DRAG_LOCATION:
        X = event.getX();
        Y = event.getY();
        //if(X>widthlayout && Y>heightlayout){
        //  temp = true;
        //}
        // Ignore the event
        return false;
      case DragEvent.ACTION_DRAG_EXITED:
        //v.invalidate();
        return false;
      case DragEvent.ACTION_DROP:
        // Gets the item containing the dragged data
        ClipData dragData = event.getClipData();
        // Gets the text data from the item.
        final String tag = dragData.getItemAt(0).getText().toString();
        View view = (View) event.getLocalState();
        ViewGroup viewgroup = (ViewGroup) view.getParent();
        viewgroup.removeView(view);
        mContainerLayout.removeView(v);
        X = event.getX();
        Y = event.getY();
        view.setX(X-(view.getWidth() / 2));
        view.setY(Y-(view.getHeight() / 2));
        mContainerLayout = (LinearLayout) v;
        mContainerLayout.addView(view);
        view.setVisibility(View.VISIBLE);
        // Invalidates the view to force a redraw
        v.invalidate();
        // Returns true. DragEvent.getResult() will return true.
        return true;
      case DragEvent.ACTION_DRAG_ENDED:
        v.invalidate();
        return false;
      default:
        break;
    }
    return false;
  }
}
