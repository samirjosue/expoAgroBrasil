package com.expoagro.expoagrobrasil.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.expoagro.expoagrobrasil.R;
import com.expoagro.expoagrobrasil.dao.UserDAO;
import com.expoagro.expoagrobrasil.model.Usuario;
import com.expoagro.expoagrobrasil.util.GoogleSignIn;
import com.expoagro.expoagrobrasil.util.Servico;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by joao on 31/07/17.
 */

public class MenuServicoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener{
    private GoogleApiClient mGoogleApiClient;
    private String uid;
    private RecyclerView recyclerView;
    private static String idClicado;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_servico);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_2);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        uid = "";
        progress = new ProgressDialog(MenuServicoActivity.this);
        progress.setCancelable(false);
        progress.setIndeterminate(true);
        progress.setMessage("Carregando anúncios...");

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener( MenuServicoActivity.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(MenuServicoActivity.this)
                .enableAutoManage(MenuServicoActivity.this, MenuServicoActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        RadioButton rdoBtnServico = (RadioButton) findViewById(R.id.rdoBtnProduto2);
        rdoBtnServico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent telaCadastrarServico = new Intent(MenuServicoActivity.this, MenuActivity.class);
                startActivity(telaCadastrarServico);
                finish();
            }
        });
        // ----------------------------------RecyclerView-----------------------------------------------------------
        progress.show();
        Thread mThread = new Thread() {
            @Override
            public void run() {
            recyclerView = (RecyclerView) findViewById(R.id.recyclerview2);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(MenuServicoActivity.this));
                DatabaseReference myref = FirebaseDatabase.getInstance().getReference("Serviço");

                final FirebaseRecyclerAdapter<Servico, ServicoViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<Servico, ServicoViewHolder>(
                        Servico.class,
                        R.layout.linha,
                        ServicoViewHolder.class,
                        myref
                ) {
                    @Override
                    protected void populateViewHolder(ServicoViewHolder viewHolder, Servico model, int posit) {

                        final String keyServico = getRef(posit).getKey();

                        viewHolder.setCategoria(model.getCategoria());
                        viewHolder.setData(model.getData());
                        viewHolder.setValor(model.getValor());
                       /* viewHolder.setFoto(model.getFoto());*/
                        viewHolder.setNome(model.getNome());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setId(keyServico);
                                //  TextView i = (TextView) findViewById(R.id.vendedor);
                                //   i.setText(model.getNome());
                                Intent intent = new Intent(MenuServicoActivity.this, VisualizarServicoActivity.class);
                                startActivity(intent);
                                //Toast.makeText(MenuActivity.this, key, Toast.LENGTH_LONG).show();
                            }
                        });


                    }

                };
                MenuServicoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(recyclerAdapter);
                        progress.dismiss();
                    }
                });
            }
        };
        mThread.start();
    }

    public static String getId() {
        return idClicado;
    }

    public static void setId(String id) {
        idClicado = id;
    }

    public static class ServicoViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView textView_nome;
        TextView textView_data;
        TextView textView_valor;
        TextView textView_categoria;
        ImageView imageView;

        public ServicoViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            textView_nome = (TextView) itemView.findViewById(R.id.nomeProduto);
            textView_data = (TextView) itemView.findViewById(R.id.dataProduto);
            textView_valor = (TextView) itemView.findViewById(R.id.valorProduto);
            textView_categoria = (TextView) itemView.findViewById(R.id.categoriaProduto);
           /* imageView = (ImageView) itemView.findViewById(R.id.fotoProduto);*/
            //textView_nome2 = (TextView) itemView.findViewById(R.id.vendedorProduto);
        }


        public void setNome(String nome) {
            textView_nome.setText(nome);
        }

        public void setData(String data) {
            textView_data.setText(data);
        }

        public void setValor(String valor) {
            textView_valor.setText(valor);
        }

        public void setCategoria(String categoria) {
            textView_categoria.setText(categoria);
        }

/*
        public void setFoto(List<String> foto) {
            if (foto == null) {
            } else {
                Picasso.with(mView.getContext())
                        .load(foto.get(0))
                        .resize(100,100)
                        .into(imageView);
            }
        }*/
    }

//---------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //getMenuInflater().inflate(R.menu.menu, menu);

        final TextView nomeUsuarioLogado = (TextView) findViewById(R.id.menu_nome);
        final TextView emailUsuarioLogado = (TextView) findViewById(R.id.menu_email);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        emailUsuarioLogado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Intent it = new Intent(MenuServicoActivity.this, LoginActivity.class);
                    startActivity(it);
                    finish();
                }
            }
        });

        UserDAO.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    if (user.getKey().equals(uid)) {
                        Usuario target = user.getValue(Usuario.class);
                        nomeUsuarioLogado.setText(target.getNome());
                        emailUsuarioLogado.setText(target.getEmail());

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_2);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_meu_perfil) {

            if(FirebaseAuth.getInstance().getCurrentUser() != null) { // Ja esta logado
                Intent telaVisualizar = new Intent(MenuServicoActivity.this, VisualizarUsuarioActivity.class);
                startActivity(telaVisualizar);
                finish();
            } else {
                Intent telaLogin = new Intent(MenuServicoActivity.this, LoginActivity.class);
                startActivity(telaLogin);
                finish();
            }
        } else if (id == R.id.menu_novo_anuncio) {

            if(FirebaseAuth.getInstance().getCurrentUser() != null) { // Ja esta logado
                Intent telaCadastrarAnuncio = new Intent(MenuServicoActivity.this, CadastroProdutoActivity.class);
                startActivity(telaCadastrarAnuncio);
                finish();
            } else {
                Intent telaLogin = new Intent(MenuServicoActivity.this, LoginActivity.class);
                startActivity(telaLogin);
                finish();
            }
        } else if (id == R.id.menu_meus_anuncios) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                System.out.println("MENU MEUS FAVORITOS"); // Ja esta logado
                Intent telaLogin = new Intent(MenuServicoActivity.this, VisualizarMeusAnunciosActivitty.class);
                startActivity(telaLogin);
                finish();
            } else {
                Intent telaLogin = new Intent(MenuServicoActivity.this, LoginActivity.class);
                startActivity(telaLogin);
                finish();
            }
        } else if (id == R.id.menu_hoje) {
            finish();
        } else if (id == R.id.menu_favoritos) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                System.out.println("MENU FAVORITOS"); // Ja esta logado
            } else {
                Intent telaLogin = new Intent(MenuServicoActivity.this, LoginActivity.class);
                startActivity(telaLogin);
                finish();
            }
        } else if (id == R.id.menu_sair) {
            GoogleSignIn.signOut(MenuServicoActivity.this, mGoogleApiClient);
        }  else if (id == R.id.menu_sobre) {
          }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_2);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        System.out.println("onConnectionFailed:" + connectionResult);
    }

}