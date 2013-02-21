package br.com.agenda.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import br.com.agenda.AgendaApp;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DownloadTask extends AsyncTask<String, Void, Bitmap> {

	private ImageView imageView;
	private int imageDefault;
	private int position;
	private AgendaApp app;

	public DownloadTask(int position, ImageView imageView, int imageDefault, AgendaApp app){
		this.imageView = imageView;
		this.imageDefault = imageDefault;
		this.position = position;
		this.app = app;
	}
	
	@Override
	protected void onPreExecute() {
		if (imageDefault != 0) {
			imageView.setImageResource(imageDefault);	
		}		
	}
	
	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap result = null;
		if (app.conectado()) {
			String url = params[0];
			if (url != null) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				InputStream in = null;
				try {
					in = app.getGerenciador().getInputStream(url);
					if (in != null) {
						int len = 0;
						while ((len = in.read(buffer)) >= 0) {
							if (this.isCancelled()) { 
								throw new Exception("Leitura abortada: " + url);
							}
							out.write(buffer, 0, len);
						}
						buffer = out.toByteArray();
						result = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
						app.setImagem(url, result);
					}
				} catch (Exception e) {
					Log.i("DownloadTask.doInBackground", "Mensagem: " + e.getMessage());				
				} finally {
					if (in != null) {
						try { 
							in.close();
							in = null;
						} catch (Exception e) {
							Log.e("DownloadTask.doInBackground", "in.close(): " + e.getMessage(), e);
						}	
					}
				}
			} else {
				if (imageDefault != 0) {
					result = BitmapFactory.decodeResource(app.getResources(), imageDefault);
					app.setImagem(url, result);	
				}
			}			
		}		
		return result;
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		int position = (Integer)imageView.getTag();
		if (this.position == position) {
			if (result != null) {
				imageView.setImageBitmap(result);	
			}
		}
	}
}