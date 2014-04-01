package groupB.hcin5300.VuforiaSample.app.VirtualButtons;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.CameraDevice.CAMERA;

import groupB.hcin5300.SampleApplication.SampleApplicationSession;
import groupB.hcin5300.SampleApplication.utils.RectCoords;
import groupB.hcin5300.SampleApplication.utils.SampleMath;
import com.qualcomm.vuforia.CameraCalibration;
import com.qualcomm.vuforia.ImageTargetResult;
import com.qualcomm.vuforia.Matrix34F;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.Vec2F;
import com.qualcomm.vuforia.Vec3F;
import com.qualcomm.vuforia.VideoBackgroundConfig;
import com.qualcomm.vuforia.VideoMode;

public class TouchProcess {
	
	   public static RectCoords agC = 
			   new RectCoords(19.2f, 35f, 37.2f, 17);//(21.2f, 33f, 35.2f, 19f);
	   public static RectCoords pbC = 
			   new RectCoords(61.7f, 20.5f, 79.7f, 1.5f);//(63.7f, 18.5f, 77.7f, 3.5f);
	
	public static void fromCameraToScreen(
			boolean inPortrait, float camX, float camY, //Vec2F cameraPoint,
			Context mContext, CameraCalibration cc, Matrix34F pose)
	{
		Vec3F point = new Vec3F(camX, camY, 0);		
		Vec2F getCamPoint = Tool.projectPoint(cc, pose, point);
				
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		float screenWidth = size.x;
		float screenHeight = size.y;
		
		Vec2F val;
		
		VideoMode videoMode = CameraDevice.getInstance().getVideoMode(CAMERA.CAMERA_DEFAULT);
		VideoBackgroundConfig config = Renderer.getInstance().getVideoBackgroundConfig();
		float xOffset = ((screenWidth - config.getSize().getData()[0]) / 2.0f + config.getPosition().getData()[0]);
		float yOffset = ((screenHeight - config.getSize().getData()[1]) / 2.0f - config.getPosition().getData()[1]);
	
		if(inPortrait)
		{
			// camera image is rotated 90 degrees
			float rotatedX = (int)(videoMode.getHeight() - getCamPoint.getData()[1]);
	        float rotatedY = getCamPoint.getData()[0];
	        val = new Vec2F(rotatedX * config.getSize().getData()[0] / 
	        		videoMode.getHeight() + xOffset,
                    rotatedY * config.getSize().getData()[1] / 
                    videoMode.getWidth() + yOffset);
		}
		else
		{
			val = new Vec2F(getCamPoint.getData()[0] * config.getSize().getData()[0] / 
					videoMode.getWidth() + xOffset,
					getCamPoint.getData()[1] * config.getSize().getData()[1] / 
					videoMode.getHeight() + yOffset);
		}
		
		String ya = Float.toString(screenHeight - val.getData()[1]);
		System.out.println(val.getData()[0]+", "+ya+")");
	}
	
	public static int isTapOnScreenInsideTarget(
			float x, float y,
			VirtualButtons mActivity, SampleApplicationSession vuforiaAppSession,
			Matrix44F modelViewMatrix)
    {
		int result = 0;
        // Here we calculate that the touch event is inside the target
        Vec3F intersection;
        // Vec3F lineStart = new Vec3F();
        // Vec3F lineEnd = new Vec3F();
        
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        intersection = SampleMath.getPointToPlaneIntersection(SampleMath
            .Matrix44FInverse(vuforiaAppSession.getProjectionMatrix()),
            modelViewMatrix, metrics.widthPixels, metrics.heightPixels,
            new Vec2F(x, y), new Vec3F(0, 0, 0), new Vec3F(0, 0, 1));
        
        // The target returns as pose the center of the trackable. The following
        // if-statement simply checks that the tap is within this range
//        if ((intersection.getData()[0] >= -(targetPositiveDimensions[target]
//            .getData()[0]))
//            && (intersection.getData()[0] <= (targetPositiveDimensions[target]
//                .getData()[0]))
//            && (intersection.getData()[1] >= -(targetPositiveDimensions[target]
//                .getData()[1]))
//            && (intersection.getData()[1] <= (targetPositiveDimensions[target]
//                .getData()[1])))
//            return true;
//        else
//            return false;
//        System.out.println(intersection.getData()[0]+", "+intersection.getData()[1]+")");
        
        if(checkBorders(intersection.getData()[0], intersection.getData()[1], agC))
        	result = 1; //Ag
        else
        {
        	if(checkBorders(intersection.getData()[0], intersection.getData()[1], pbC))
        		result = 2; //Pb
        }
        return result;
    }
	
	public static boolean checkBorders(float ix, float iy, RectCoords rc)
	{
		if(ix >= rc.left &&
				ix <= rc.right &&
				iy <= rc.top &&
				iy >= rc.bottom)
		{
			return true;
		}
		return false;
	}

}
