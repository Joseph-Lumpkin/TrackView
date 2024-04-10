package com.josephlumpkin.tracking;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Track View <p>
 * This view allows the user to see their position on a track
 * when supplied the percentage of lap completion.
 */
public class TrackView extends View {

    //*********************************
    // Private Properties
    //*********************************
    /** Path Definitions, Milestones and Variables */
    private Path trackPath;
    private Path cursorPath;
    private Path pacerPath;
    private float cornerRadius = 360f;      // Corner radius of the standard oval track
    private float startingPosition = .25f;  // Bottom-Center of the standard oval track
    private float cursorProgress = .00f;    // Initial progress of the cursor
    private float pacerProgress = .00f;     // Initial progress of the pacer
    private String pacerText = "";

    /** Paints */
    private Paint trackPaint;
    private Paint cursorPaint;
    private Paint pacerPaint;
    private Paint pacerTextPaint;
    private Paint whiteDottedPaint;
    private Paint whiteFillPaint;

    /** Visibilities */
    private int mCursorVisibility = View.VISIBLE;
    private int mPacerVisibility = View.VISIBLE;

    public TrackView(Context context) {
        super(context);
        init();
    }

    public TrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        trackPaint = new Paint();
        cursorPaint = new Paint();
        pacerPaint = new Paint();
        pacerTextPaint = new Paint();
        whiteFillPaint = new Paint();
        whiteDottedPaint = new Paint();

        trackPaint.setAntiAlias(true);
        cursorPaint.setAntiAlias(true);
        pacerPaint.setAntiAlias(true);
        pacerTextPaint.setAntiAlias(true);
        whiteFillPaint.setAntiAlias(true);
        whiteDottedPaint.setAntiAlias(true);

        trackPaint.setStyle(Paint.Style.STROKE);
        cursorPaint.setStyle(Paint.Style.FILL);
        pacerPaint.setStyle(Paint.Style.FILL);
        pacerTextPaint.setStyle(Paint.Style.STROKE);
        whiteFillPaint.setStyle(Paint.Style.FILL);
        whiteDottedPaint.setStyle(Paint.Style.STROKE);

        trackPaint.setColor(getResources().getColor(R.color.colorPrimaryDark));
        cursorPaint.setColor(Color.BLACK);
        pacerPaint.setColor(getResources().getColor(R.color.colorAccent));
        pacerTextPaint.setColor(Color.BLACK);
        whiteFillPaint.setColor(Color.WHITE);
        whiteDottedPaint.setColor(Color.WHITE);
        whiteDottedPaint.setStrokeWidth(4f);
        whiteDottedPaint.setPathEffect(new DashPathEffect(new float[]{10f, 10f}, 2f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // The order of drawing operations is important
        drawTrack(canvas);
        drawFinishLine(canvas);
        drawCursor(canvas);
        drawPacer(canvas);
    }

    /**
     * Draw the track onto the canvas.
     *
     * @param canvas    - Canvas to draw the track onto.
     */
    private void drawTrack(Canvas canvas) {
        trackPath = new Path();
        trackPaint.setStrokeWidth((float) getWidth() / 12);
        /* Create the paths for the track, cursor and pacer
        * This should support custom tracks like using a
        * list of coordinates to draw a custom track or
        * extract the path data from a vector image and place here. */
        trackPath.addRoundRect(
                trackPaint.getStrokeWidth() / 3,
                trackPaint.getStrokeWidth() / 3,
                getWidth() - trackPaint.getStrokeWidth(),
                getHeight() - trackPaint.getStrokeWidth(),
                cornerRadius,
                cornerRadius,
                Path.Direction.CCW
        );
        trackPath.close();
        canvas.translate(trackPaint.getStrokeWidth() / 3, trackPaint.getStrokeWidth() / 3);
        canvas.drawPath(trackPath, trackPaint);
    }


    /**
     * Draw the finish line onto the canvas.
     *
     * @param canvas    - Canvas to draw the finish line onto.
     */
    private void drawFinishLine(Canvas canvas) {
        Path path = new Path();
        float[] finishLine = getFinishLine();
        path.moveTo(finishLine[0], finishLine[1]);
        path.lineTo(finishLine[2], finishLine[3]);
        path.close();
        canvas.drawPath(path, whiteDottedPaint);
    }


    /**
     * Draw the cursor onto the canvas.
     *
     * @param canvas    - Canvas to draw the cursor onto.
     */
    private void drawCursor(Canvas canvas) {
        if (mCursorVisibility != View.VISIBLE)
            return;
        cursorPath = new Path();
        cursorPath.addRoundRect(
                trackPaint.getStrokeWidth() / 3,
                trackPaint.getStrokeWidth() / 3,
                getWidth() - (trackPaint.getStrokeWidth()),
                getHeight() - (trackPaint.getStrokeWidth()),
                cornerRadius,
                cornerRadius,
                Path.Direction.CCW
        );
        cursorPath.close();
        Path path = new Path();
        PathMeasure pm = new PathMeasure(cursorPath, true);
        float[] cursorStart = new float[2];
        float lapLength = pm.getLength();
        float distance = (startingPosition * lapLength + cursorProgress * lapLength) % lapLength;
        Log.d("TrackView", "Cursor progress: " + cursorProgress + " Distance: " + distance);
        pm.getPosTan(distance, cursorStart, null);
        path.addCircle(cursorStart[0], cursorStart[1], trackPaint.getStrokeWidth() / 2.5f, Path.Direction.CW);
        path.close();
        canvas.drawPath(path, cursorPaint);
        path = new Path();
        path.addCircle(cursorStart[0], cursorStart[1], (trackPaint.getStrokeWidth() / 2.5f) - 5, Path.Direction.CW);
        path.close();
        canvas.drawPath(path, whiteFillPaint);
    }


    /**
     * Draw the pacer onto the canvas.
     *
     * @param canvas    - Canvas to draw the pacer onto.
     */
    private void drawPacer(Canvas canvas) {
        if (mPacerVisibility != View.VISIBLE)
            return;
        pacerPath = new Path();
        pacerPath.addRoundRect(
                trackPaint.getStrokeWidth(),
                trackPaint.getStrokeWidth(),
                getWidth() - (trackPaint.getStrokeWidth() * 1.5f),
                getHeight() - (trackPaint.getStrokeWidth() * 1.5f),
                cornerRadius,
                cornerRadius,
                Path.Direction.CCW
        );
        Path path = new Path();
        pacerPath.close();
        float[] pacerCoord = new float[2];
        float[] tan = new float[2];
        PathMeasure pm = new PathMeasure(cursorPath, true);
        float lapLength = pm.getLength();
        float distance = (startingPosition * lapLength + pacerProgress * lapLength) % lapLength;
        Log.d("TrackView", "Pacer progress: " + pacerProgress + " Distance: " + distance);
        pm.getPosTan((startingPosition * lapLength + pacerProgress * lapLength) % lapLength, pacerCoord, tan);
        // Offset the pacer to ride the inside edge of the track
        pacerCoord = getAngledCoords(pacerCoord, (trackPaint.getStrokeWidth() / 2), tan);
        path.addCircle(pacerCoord[0], pacerCoord[1], trackPaint.getStrokeWidth() / 6, Path.Direction.CW);
        path.close();
        canvas.drawPath(path, pacerPaint);
        path = new Path();
        path.addCircle(pacerCoord[0], pacerCoord[1], (trackPaint.getStrokeWidth() / 6) - 5, Path.Direction.CW);
        path.close();
        canvas.drawPath(path, whiteFillPaint);
        path = new Path();
        Rect bounds = new Rect();
        pacerPaint.getTextBounds(pacerText, 0, pacerText.length(), bounds);
        path.moveTo(pacerCoord[0]-(bounds.width()/2), pacerCoord[1]);
        canvas.drawText(pacerText, pacerCoord[0]-bounds.width()/2, pacerCoord[1]+bounds.height()/2, pacerTextPaint);
    }


    /**
     * Find an offset point's screen coordinates
     * along the normal line to an origin point.
     *
     * @param origin    - Origin point as [x, y].
     * @param offset    - Offset amount to be applied as an amount of
     *                      pixels on the screen from the origin.
     * @param tan       - Slope of the tangent line to the origin point as [x, y].
     * @return offsetCoordinates    - Coordinates for a point
     *                                  offset from the origin, along the normal line.
     */
    private float[] getAngledCoords(float[] origin, float offset, float[] tan) {
        // Reflect the tangent line's slope to find the normal line
        float[] normalLine = new float[2];
        normalLine[0] = tan[1];
        normalLine[1] = tan[0];

        // Calculate the offset coordinate distance for x and add to the x coord
        float offsetCoord = offset * normalLine[0];
        origin[0] += offsetCoord;

        // Calculate the offset coordinate distance for y and add to the y coord
        offsetCoord = offset * normalLine[1];
        offsetCoord = offsetCoord * -1f;
        origin[1] += offsetCoord;

        return origin; // Return the modified origin point, now a destination point
    }

    /**
     * Get a finish line starting and ending coordinates for the track.
     *
     * @return finishLine   - Finish line for the track as
     *                          [startX, startY, endX, endY]
     */
    private float[] getFinishLine() {
        float[] origin = new float[2];
        float[] tan = new float[2];
        float[] line = new float[4];
        PathMeasure pm = new PathMeasure(trackPath, true);

        // Get the starting position and tangent line to the origin
        pm.getPosTan(startingPosition * pm.getLength(), origin, tan);

        // Use an offset along the normal line to determine the starting point for a finish line
        getAngledCoords(origin, (getWidth() / 16) / 2, tan);
        // Load the starting point into the line
        line[0] = origin[0];
        line[1] = origin[1];

        // Use an offset along the normal line to determine the ending point for the finish line
        getAngledCoords(origin, -1 * (getWidth() / 16), tan);
        // Load the finish point into the line
        line[2] = origin[0];
        line[3] = origin[1];
        Log.d(
                "TrackView",
                "Finish line coordinates=["
                        + line[0] + ", " + line[1] + "] ["
                        + line[2] + ", " + line[3] + "]"
        );
        return line;
    }

    public Path getTrackPath() {
        return cursorPath;
    }

    public Path getCursorPath() {
        return cursorPath;
    }

    public Path getPacerPath() {
        return cursorPath;
    }

    public Paint getTrackPaint() {
        return trackPaint;
    }

    public Paint getCursorPaint() {
        return cursorPaint;
    }

    public Paint getPacerPaint() {
        return pacerPaint;
    }

    public void setTrackPaint(Paint paint) {
        trackPaint = paint;
        invalidate();
    }

    public void setCursorPaint(Paint paint) {
        cursorPaint = paint;
        invalidate();
    }

    public void setPacerPaint(Paint paint) {
        pacerPaint = paint;
        invalidate();
    }

    /**
     * Set the progress of the cursor.
     *
     * @param progress  Percentage of lap completion for this track view
     */
    public void setCursorProgress(float progress) {
        //TODO Add interpolation engine for smooth, custom animations
        cursorProgress = progress % 1f;
        invalidate();
    }

    /**
     * Set the visibility of the pacer dot.
     *
     * @param visibility    The desired View.Visibility state<br>
     *                      View.VISIBLE = Visible <br>
     *                      Anything else = Gone
     */
    public void setCursorVisibility(int visibility) {
        mCursorVisibility = visibility;
        invalidate();
    }

    public void setPacerProgress(float progress) {
        //TODO Add interpolation engine for smooth, custom animations
        pacerProgress = progress % 1f;
        invalidate();
    }

    /**
     * Set the visibility of the pacer dot.
     *
     * @param visibility    The desired View.Visibility state<br>
     *                      View.VISIBLE = Visible <br>
     *                      Anything else = Gone
     */
    public void setPacerVisibility(int visibility) {
        mPacerVisibility = visibility;
        invalidate();
    }

    public void setPacerText(String text) {
        pacerText = text;
        invalidate();
    }
}