package br.com.agenda;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import br.com.agenda.bean.Contato;
import br.com.agenda.bean.ListaDeContatos;
import br.com.agenda.util.AdapterListaDeContatos;
import br.com.agenda.util.GerenciadorDeDados;

public class ListaDeContatosActivity extends Activity implements OnClickListener{
	
	private ListView listView;
	
	private AgendaApp app;
	
	private GerenciadorDeDados gerenciador;
	
	private ProgressDialog progressDialog;
	
	private CarregaContatos carregaContatos;
	
	private ListaDeContatos listaDeContatos;
	
	private AdapterListaDeContatos adapter;
	
	private Button btnSair;
	
	private Button btnBuscar;
	
	private Button btnCarregarMais;
	
	private BuscarContato buscarContato;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listadecontatos);
		
		app = (AgendaApp)getApplication();
		gerenciador = app.getGerenciador();
		
		btnCarregarMais = new Button(this);
		btnCarregarMais.setText("Carregar mais");
		btnCarregarMais.setTextColor(Color.BLACK);
		btnCarregarMais.setOnClickListener(this);
		btnCarregarMais.setVisibility(View.INVISIBLE);
		
		listView = (ListView)findViewById(R.listaDeContatos.listView);
		listView.addFooterView(btnCarregarMais);
		adapter = new AdapterListaDeContatos(this, android.R.layout.simple_list_item_multiple_choice, app);
		listView.setAdapter(adapter);
		listView.requestFocus();
		
		btnSair = (Button)findViewById(R.listaDeContatos.btnSair);
		btnSair.setOnClickListener(this);
		
		btnBuscar = (Button)findViewById(R.listaDeContatos.btnBuscar);
		btnBuscar.setOnClickListener(this);
		
		carregaContatos = new CarregaContatos();
		carregaContatos.execute(1);
	}
	
	@Override
	public void finish() {
		if (carregaContatos != null) {
			if (carregaContatos.isCancelled()) {
				carregaContatos.cancel(true);
			}
		}
		if (adapter != null) {
			adapter.stopDownloads();
		}
		if (buscarContato != null) {
			if (!buscarContato.isCancelled()) {
				buscarContato.cancel(true);
			}
		}
		super.finish();
	}
	
	public void onClick(View v) {
		if(v.getId() == btnCarregarMais.getId()){
			carregaContatos = new CarregaContatos();
			carregaContatos.execute(listaDeContatos.getPaginaAtual()+1);
		} else {
			switch (v.getId()) {
				case R.listaDeContatos.btnBuscar:
					buscar();
					break;
					
				case R.listaDeContatos.btnSair:
					app.sair();
					
					Intent intent = new Intent(this, HomeActivity.class);
					startActivity(intent);
					
					finish();					
					break;
				
				case R.itemListaDeContatos.btnEditar:
					mostarInformacoes((Button)v);
					break;
					
				case R.informacoes.btnFechar:
					((Dialog)v.getTag()).dismiss();
					break;
					
				case R.buscar.btnConfirmar:
					Dialog buscar = (Dialog)v.getTag();
										
					EditText edtNome = (EditText)buscar.findViewById(R.buscar.edtNome);
					
					buscarContato = new BuscarContato();
					buscarContato.execute(edtNome.getText().toString());
					
					buscar.dismiss();
					break;
			}			
		}	
	}
	
	private void buscar() {
		Dialog buscar = new Dialog(this);
		buscar.setTitle("Buscar contatos");
		buscar.setContentView(R.layout.buscar);
	
		Button btnConfirmar = (Button)buscar.findViewById(R.buscar.btnConfirmar);
		btnConfirmar.setOnClickListener(this);
		btnConfirmar.setTag(buscar);
		
		buscar.show();
	}

	private void mostarInformacoes(View view) {
		Contato contato = (Contato)view.getTag();
		
		List<String> telefones = new ArrayList<String>();
		for (int i = 0; i < contato.getTelefones().size(); i++) {
			telefones.add(contato.getTelefones().get(i).getNumero());
		}
		
		List<String> emails = new ArrayList<String>();
		for (int i = 0; i < contato.getEmails().size(); i++) {
			emails.add(contato.getEmails().get(i).getEmail());
		}
		
		Dialog informacoes = new Dialog(this);
		informacoes.setContentView(R.layout.informacoes);
		informacoes.setTitle(contato.getNome());
		
		Spinner spnTelefone = (Spinner)informacoes.findViewById(R.informacoes.spnTelefone);
		spnTelefone.setAdapter(new ArrayAdapter<String>(this, R.layout.item_spinner, telefones));
		
		Spinner spnEmail = (Spinner)informacoes.findViewById(R.informacoes.spnEmail);
		spnEmail.setAdapter(new ArrayAdapter<String>(this, R.layout.item_spinner, emails));
		
		Button btnFechar = (Button)informacoes.findViewById(R.informacoes.btnFechar);
		btnFechar.setOnClickListener(this);
		btnFechar.setTag(informacoes);		
		
		informacoes.show();
	}
	
	private class CarregaContatos extends AsyncTask<Integer, Void, ListaDeContatos>{
		
		public CarregaContatos(){
			mensagem = "";
		}

		private String mensagem;
		
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(ListaDeContatosActivity.this);
			progressDialog.setTitle("Agenda");
			progressDialog.setMessage("Verificando...");			
			progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancelar", new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					if (!carregaContatos.isCancelled()) {
						carregaContatos.cancel(true);
					}					
				}
			});
			progressDialog.show();
		}
		
		@Override
		protected ListaDeContatos doInBackground(Integer... params) {		
			ListaDeContatos listaDeContatos = null;
			
			if (app.conectado()) {
				try {
					listaDeContatos = gerenciador.listarContatos(params[0], 5);
				} catch (ClientProtocolException cpe) {
					Log.e("ListaDeContatosActivity.CarregaContatos.doInBackground()", cpe.getMessage(), cpe);
					mensagem = "Ocorreu um erro ao fazer a requisição ao servidor.";
				} catch (IOException ioe) {
					Log.e("ListaDeContatosActivity.CarregaContatos.doInBackground()", ioe.getMessage(), ioe);
					mensagem = "Ocorreu um erro ler os dados do servidor.";
				} catch (JSONException jsone) {
					Log.e("ListaDeContatosActivity.CarregaContatos.doInBackground()", jsone.getMessage(), jsone);
					mensagem = "Ocorreu um erro na leitura do json.";
				}	
			} else {
				mensagem = "Falha na conexão.\nTente novamente mais tarde.";
			}
			
			return listaDeContatos;
		}
		
		@Override
		protected void onPostExecute(ListaDeContatos result) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
				
				if (!mensagem.equals("")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(ListaDeContatosActivity.this);
					builder.setTitle("Agenda App");
					builder.setMessage(mensagem);
					builder.create();
					builder.show();
				} else if (result == null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(ListaDeContatosActivity.this);
					builder.setTitle("Agenda App");
					builder.setMessage("Não foi possível conectar-se ao servidor. Tente novamente mais tarde.");
					builder.create();
					builder.show();
				} else {
					if (listaDeContatos == null) {
						listaDeContatos = result;	
					} else {
						listaDeContatos.getContatos().addAll(result.getContatos());
						listaDeContatos.setPaginaAtual(result.getPaginaAtual());
					}
					adapter.addAll(result.getContatos());
					adapter.notifyDataSetChanged();
					if (listaDeContatos.getPaginaAtual() < listaDeContatos.getQtPaginas()) {
						btnCarregarMais.setVisibility(View.VISIBLE);
					} else {
						btnCarregarMais.setVisibility(View.INVISIBLE);
					}
				}			
			}
		}		
	}//CarregaContatos
	
	private class BuscarContato extends AsyncTask<String, Void, List<Contato>>{

		public BuscarContato(){
			mensagem = "";
		}
		
		private String mensagem;
		
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(ListaDeContatosActivity.this);
			progressDialog.setTitle("Agenda");
			progressDialog.setMessage("Buscando...");			
			progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancelar", new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					if (!buscarContato.isCancelled()) {
						buscarContato.cancel(true);
					}					
				}
			});
			progressDialog.show();
		}
		
		@Override
		protected List<Contato> doInBackground(String... params) {
			List<Contato> contatos = null;
			
			if (app.conectado()) {
				try {
					contatos = gerenciador.buscarContatos(params[0]).getContatos();
				} catch (ClientProtocolException cpe) {					
					Log.e("ListaDeContatosActivity.BuscarContato.doInBackground()", cpe.getMessage(), cpe);
					mensagem = "Ocorreu um erro ao fazer a requisição ao servidor.";
				} catch (UnsupportedEncodingException uee) {
					Log.e("ListaDeContatosActivity.BuscarContato.doInBackground()", uee.getMessage(), uee);
					mensagem = "Texto muito longo para fazer a pesquisa.";
				} catch (IOException ioe) {
					Log.e("ListaDeContatosActivity.BuscarContato.doInBackground()", ioe.getMessage(), ioe);
					mensagem = "Ocorreu um erro ler os dados do servidor.";
				} catch (JSONException jsone) {
					Log.e("ListaDeContatosActivity.BuscarContato.doInBackground()", jsone.getMessage(), jsone);
					mensagem = "Ocorreu um erro na leitura do json.";
				}				
			}
			
			return contatos;
		}
		
		@Override
		protected void onPostExecute(List<Contato> result) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			
			if (!mensagem.equals("")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ListaDeContatosActivity.this);
				builder.setTitle("Agenda App");
				builder.setMessage(mensagem);
				builder.create();
				builder.show();
			} else if (result == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ListaDeContatosActivity.this);
				builder.setTitle("Agenda App");
				builder.setMessage(mensagem);
				builder.create();
				builder.show();				
			} else {
				Dialog resultadoBusca = new Dialog(ListaDeContatosActivity.this);
				resultadoBusca.setTitle("Resultado");
				resultadoBusca.setContentView(R.layout.resultado_busca);
				
				ListView listView = (ListView)resultadoBusca.findViewById(R.resultadoBusca.lsvContato);
				listView.setAdapter(
						new AdapterListaDeContatos(
								ListaDeContatosActivity.this, 
								android.R.layout.simple_list_item_multiple_choice, 
								result, 
								app
						)
				);
				
				resultadoBusca.show();
			}			
		}
	}//BuscarContato
}//ListaDeContatosActivity