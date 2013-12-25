package com.page.curl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class Page {

	public final int GRID = 25;
	private final float RADIUS = 0.2f;
	public float curlCirclePosition = 25;

	private FloatBuffer vertexBuffer;
	private FloatBuffer textureBuffer;
	private ShortBuffer indexBuffer;

	private int[] textures = new int[1];
	private float vertices[] = new float[(GRID+1)*(GRID+1)*3];
	private float texture[] = new float[(GRID+1)*(GRID+1)*2];
	private short indices[] = new short[GRID*GRID*6];


	public void calculateVerticesCoords(){
		
		float angle = 1.0f/((float)GRID*RADIUS);
		for(int row=0;row<=GRID;row++)
			for(int col=0;col<=GRID;col++){
				int pos = 3*(row*(GRID+1)+col);

				if(col>curlCirclePosition){
					vertices[pos]=(float) ((curlCirclePosition/GRID)+RADIUS*Math.sin(angle*(col-curlCirclePosition)));
					vertices[pos+2]=(float) (RADIUS*(1-Math.cos(angle*(col-curlCirclePosition))));

				}else{
					vertices[pos]=(float)col/(float)GRID;
					vertices[pos+2]=0;
				}
				
				vertices[pos+1]=(float)row/(float)GRID;
			}

		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		vertexBuffer = byteBuf.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

	}

	private void calculateFacesCoords(){

		for(int row=0;row<GRID;row++)
			for(int col=0;col<GRID;col++){
				int pos = 6*(row*GRID+col);

				indices[pos] = (short) (row*(GRID+1)+col);
				indices[pos+1]=(short) (row*(GRID+1)+col+1);
				indices[pos+2]=(short) ((row+1)*(GRID+1)+col);

				indices[pos+3]=(short) (row*(GRID+1)+col+1);
				indices[pos+4]=(short) ((row+1)*(GRID+1)+col+1);
				indices[pos+5]=(short) ((row+1)*(GRID+1)+col);

			}
	}

	private void calculateTextureCoords(){

		for(int row=0;row<=GRID;row++)
			for(int col=0;col<=GRID;col++){
				int pos = 2*(row*(GRID+1)+col);

				texture[pos]=col/(float)GRID;
				texture[pos+1]=1-row/(float)GRID;
			}
	}

	public Page() {
		calculateVerticesCoords();
		calculateFacesCoords();
		calculateTextureCoords();

		ByteBuffer byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.put(texture);
		textureBuffer.position(0);

		byteBuf = ByteBuffer.allocateDirect(indices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		indexBuffer = byteBuf.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
	}

	public void draw(GL10 gl) {
		calculateVerticesCoords();	
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glFrontFace(GL10.GL_CCW);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	public void loadGLTexture(GL10 gl, Context context) {
		InputStream is = context.getResources().openRawResource(R.drawable.page);
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(is);

		} finally {
			try {
				is.close();
				is = null;
			} catch (IOException e) {
			}
		}

		gl.glGenTextures(1, textures, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		bitmap.recycle();
	}
}
