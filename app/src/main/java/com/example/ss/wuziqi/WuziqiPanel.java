package com.example.ss.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ss on 2017/5/31.
 */

public class WuziqiPanel extends View {

    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE = 10;
    private int MAX_COUNT_INT_LINE=5;

    private Paint mPaint = new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;

    //白棋先手，当前轮到白棋
    private boolean mIsWhite = true;

    //存储棋子坐标
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();

    private boolean mIsGameOver;
    private boolean mIsWhiteWinner;


    //棋子大小是行高3/4
    public WuziqiPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44ff0000);
        init();
    }

    //paint初始化
    private void init() {
        //半透明黑色：灰色
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        //棋子初始化
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.wen);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        setMeasuredDimension(width, width);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        /*棋子大小跟随行高大小动态变化
        int转换后面是式子要加括号*/
        int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);
        //参数：原本加载的图，目标宽度,目标高度，filter
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(mIsGameOver)
        {
            return false;
        }

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            //每次点击的位置不一样，不能作为存储位置
            //Point p=new Point(x,y);useless!
            Point p = getValidPoint(x, y);

            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false;
            }

            if (mIsWhite) {
                mWhiteArray.add(p);
            } else {
                mBlackArray.add(p);
            }
            invalidate();
            //下完一步后要改变先后手
            mIsWhite = !mIsWhite;

        }

        return true;
    }

    private Point getValidPoint(int x, int y) {//得到整数点的位置
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画棋板
        drawBoard(canvas);
        //画棋子
        drawPieces(canvas);
        //检测是否结束
        checkGameOver();
    }

    private void checkGameOver()
    {
        boolean whiteWin= checkFiveInLine(mWhiteArray);
        boolean blackWin= checkFiveInLine(mBlackArray);

        if(whiteWin || blackWin)
        {
            mIsGameOver=true;
            mIsWhiteWinner=whiteWin;

            String text=mIsWhiteWinner?"白棋胜利":"黑棋胜利";
            //这是一个类，不是活动，不能用this,用getContext（）
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkFiveInLine(List<Point> points)
    {
        for(Point p:points)
        {
            int x=p.x;
            int y=p.y;

            boolean win=checkHorizontal(x,y,points);
            if(win) return true;

            win=checkVertical(x,y,points);
            if(win) return true;

            win=checkLeftDiagonal(x,y,points);
            if(win) return true;

            win=checkRightDiagonal(x,y,points);
            if(win) return true;
        }

        return false;
    }
    /*应该把这些检测代码抽取到一个工具类中，这样主代码就少了
    判断x,y位置的棋子是否横向有相邻的五个一致*/
    private boolean checkHorizontal(int x, int y, List<Point> points)
    {
        int count=1;
        //判断左面是否有5个
        for(int i=1;i<MAX_COUNT_INT_LINE;i++)
        {
            if(points.contains(new Point(x-i,y))){
                count++;
            }else{
                break;
            }
            if(count == MAX_COUNT_INT_LINE) return true;
        }
        //判断右面是否连成5个
        for(int i=1;i<MAX_COUNT_INT_LINE;i++)
        {
            if(points.contains(new Point(x+i,y))){
                count++;
            }else{
                break;
            }
            if(count == MAX_COUNT_INT_LINE) return true;
        }

        return false;
    }

    /*
    判断x,y位置的棋子是否纵向有相邻的五个一致*/
    private boolean checkVertical(int x, int y, List<Point> points)
    {
        int count=1;
        //判断上面是否有5个
        for(int i=1;i<MAX_COUNT_INT_LINE;i++)
        {
            if(points.contains(new Point(x,y-i))){
                count++;
            }else{
                break;
            }
            if(count == MAX_COUNT_INT_LINE) return true;
        }
        //判断右面是否连成5个
        for(int i=1;i<MAX_COUNT_INT_LINE;i++)
        {
            if(points.contains(new Point(x,y+i))){
                count++;
            }else{
                break;
            }
            if(count == MAX_COUNT_INT_LINE) return true;
        }

        return false;
    }

    /*
    判断x,y位置的棋子是否左斜有相邻的五个一致*/
    private boolean checkLeftDiagonal(int x, int y, List<Point> points)
    {
        int count=1;
        //判断左下是否有5个
        for(int i=1;i<MAX_COUNT_INT_LINE;i++)
        {
            if(points.contains(new Point(x-i,y+i))){
                count++;
            }else{
                break;
            }
            if(count == MAX_COUNT_INT_LINE) return true;
        }
        //判断右上是否连成5个
        for(int i=1;i<MAX_COUNT_INT_LINE;i++)
        {
            if(points.contains(new Point(x+i,y-i))){
                count++;
            }else{
                break;
            }
            if(count == MAX_COUNT_INT_LINE) return true;
        }

        return false;
    }

    /*
    判断x,y位置的棋子是否右斜有相邻的五个一致*/
    private boolean checkRightDiagonal(int x, int y, List<Point> points)
    {
        int count=1;
        //判断左上是否有5个
        for(int i=1;i<MAX_COUNT_INT_LINE;i++)
        {
            if(points.contains(new Point(x-i,y-i))){
                count++;
            }else{
                break;
            }
            if(count == MAX_COUNT_INT_LINE) return true;
        }
        //判断右下是否连成5个
        for(int i=1;i<MAX_COUNT_INT_LINE;i++)
        {
            if(points.contains(new Point(x+i,y+i))){
                count++;
            }else{
                break;
            }
            if(count == MAX_COUNT_INT_LINE) return true;
        }

        return false;
    }



    //画棋子
    private void drawPieces(Canvas canvas) {
        //为了考虑效率，只调用一次mWhiteArray.size()
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            //参数：棋子，横坐标，纵坐标，null
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (whitePoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }

        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }

    }

    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) lineHeight / 2;
            int endX = (int) (w - lineHeight / 2);
            int y = (int) ((i + 0.5) * lineHeight);
            //绘制横线
            canvas.drawLine(startX, y, endX, y, mPaint);
            //绘制纵线，与横线对应
            canvas.drawLine(y, startX, y, endX, mPaint);

        }
    }
    //再来一局
    public void start()
    {
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver=false;
        mIsWhiteWinner=false;
        invalidate();
    }


    private static final String INSTANCE="instance";
    private static final String INSTANCE_GAME_OVER="instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY="instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY="instance_black_array";


    //保存实例，在旋转或者暂停时
    @Override
    protected Parcelable onSaveInstanceState()
    {
        Bundle bundle=new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER,mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);

        return bundle;
    }
    //恢复实例
    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if(state instanceof Bundle)
        {
            Bundle bundle=(Bundle)state;
            mIsGameOver=bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray=bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray=bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);

            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));

            return;
        }
        super.onRestoreInstanceState(state);
    }

}








