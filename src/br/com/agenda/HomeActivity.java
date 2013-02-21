package br.com.agenda;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import org.xml.sax.SAXException;
import br.com.agenda.R;
import br.com.agenda.util.GerenciadorDeDados;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class HomeActivity extends Activity implements OnClickListener {
	
	private EditText edtLogin;
	
	private EditText edtSenha;
	
	private Button btnConfirmar;
	
	private AgendaApp app;
	
	private GerenciadorDeDados gerenciador;
	
	private VerificarDados verificarDados;
	
	private ProgressDialog progressDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        app = (AgendaApp)getApplication();
        
        if (app.logado()) {
			Intent intent = new Intent(this, ListaDeContatosActivity.class);
			startActivity(intent);
			finish();
		} else {
	        gerenciador = app.getGerenciador(); 
	        
	        edtLogin = (EditText)findViewById(R.home.edtLogin);
	        edtSenha = (EditText)findViewById(R.home.edtSenha);
	        btnConfirmar = (Button)findViewById(R.home.btnConfirmar);
	        btnConfirmar.setOnClickListener(this);	
		}
    }
    
    @Override
    public void finish() {
    	if (verificarDados != null) {
    		if (!verificarDados.isCancelled()) {
    			verificarDados.cancel(true);
			}
		}
    	super.finish();
    }

	public void onClick(View v) {
		verificarDados = new VerificarDados();
		verificarDados.execute();
	}
	
	private class VerificarDados extends AsyncTask<Void, Void, String> {

		public VerificarDados(){
			mensagem = "";
		}
		
		private String mensagem;
		
		@Override
		protected void onPreExecute() {			
			progressDialog = new ProgressDialog(HomeActivity.this);
			progressDialog.setTitle("Agenda");
			progressDialog.setMessage("Verificando...");			
			progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancelar", new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					if (!verificarDados.isCancelled()) {
						verificarDados.cancel(true);
					}					
				}
			});
			progressDialog.show();
		}
		
		@Override
		protected String doInBackground(Void... params) {
			String result = null;
			if (app.conectado()) {
				try {
					result = gerenciador.login(edtLogin.getText().toString(), edtSenha.getText().toString());
				} catch (ClientProtocolException cpe) {
					Log.e("HomeActivity.VerificarDados.doInBackground()", "Erro ao executar getInputStream().", cpe);
					mensagem = "Ocorreu um erro ao fazer a requisição ao servidor.";
					result = "";
				} catch (IOException ioe) {
					Log.e("HomeActivity.VerificarDados.doInBackground()", ioe.getMessage(), ioe);
					mensagem = "Ocorreu um erro ler os dados do servidor.";
					result = "";
				} catch (SAXException saxe) {
					Log.e("HomeActivity.VerificarDados.doInBackground()", "Erro  ao executar parseLogin().", saxe);
					mensagem = "Ocorreu um erro na leitura do xml.";
					result = "";
				} finally {
					if (result == null) {
						mensagem = "Não foi possível conectar-se ao servidor. Tente novamente mais tarde.";
					}
				}
			} else {
				mensagem = "Problema na conexão. Verifique se você está conectado à Internet.";
			}			
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
				
				if (!mensagem.equals("")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
					builder.setTitle("Agenda App");
					builder.setMessage(mensagem);
					builder.create();
					builder.show();
				} else if (!result.contains("sucesso")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
					builder.setTitle("Agenda App");
					builder.setMessage(result);
					builder.create();
					builder.show();
				} else {
					SharedPreferences.Editor edit = app.getPreferences().edit();
					edit.putString("login", edtLogin.getText().toString());
					edit.putString("senha", edtSenha.getText().toString());
					edit.commit();

					Intent intent = new Intent(HomeActivity.this, ListaDeContatosActivity.class);
					startActivity(intent);
					finish();
				}
			}
		}				
	}//VerificarDados
}//HomeActivity