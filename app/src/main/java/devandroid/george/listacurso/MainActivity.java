package devandroid.george.listacurso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //private static final String BASE_URL = "https://api.openai.com";
    private static final String API_KEY = "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";
    private static final String MODEL = "gpt-3.5-turbo";
    private static final String CONTENT_TYPE = "application/json";
    private String prompt = "Resuma o livro ";
    private EditText nomeAutor;
    private EditText nomeLivro;

    private ToggleButton toggleDetalhes;
    private ToggleButton toggleEmocao;
    private ToggleButton toggleCuriosidades;
    private TextView textView;
    private static final String NAME_FILE = "/resumeAI-";
    private static final String PDF_EXTENSION = ".pdf";
    private static final String FILE_PROVIDER = ".provider";
    private static final String APPLICATION_PDF = "application/pdf";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            String permissao[] = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            };
        }

        Button resumeAI = findViewById(R.id.resumeAI);

        resumeAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickButton();
            }
        });

    }

    private void clickButton() {
        //  Thread thread = new Thread(new Runnable() {
        //    @Override
        //  public void run() {

        nomeLivro = (EditText) findViewById(R.id.nomeLivro);
        nomeAutor = (EditText) findViewById(R.id.nomeAutor);
        toggleCuriosidades = (ToggleButton) findViewById(R.id.toggleCuriosidades);
        toggleDetalhes = (ToggleButton) findViewById(R.id.toggleDetalhes);
        toggleEmocao = (ToggleButton) findViewById(R.id.toggleEmocao);
        textView = (TextView) findViewById(R.id.textView);


        if (Objects.nonNull(nomeLivro)) {
            prompt += nomeLivro.getText().toString();
        }
        if (Objects.nonNull(nomeAutor)) {
            prompt += " do Autor ";
            prompt += nomeAutor.getText().toString();
        }
        if (toggleDetalhes.isChecked()) {
            prompt += ", com mais detalhes";
        }
        if (toggleEmocao.isChecked()) {
            prompt += ", com mais emoção";
        }
        if (toggleCuriosidades.isChecked()) {
            prompt += ", com mais curiosidades";
        }

                /*OpenAiService service = new OpenAiService(API_KEY);

                CompletionRequest request = CompletionRequest.builder()
                        .prompt("Crie uma frase engraçada")
                        .model(MODEL)
                        .echo(true)
                        .build();

                System.out.println(service.createCompletion(request).getChoices());*/

        textView.setText(prompt);


        //GerarPDF gerarPDF = new GerarPDF();
        //gerarPDF.gerarPDF(prompt, nomeLivro.getText().toString());


        try {

            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + NAME_FILE + nomeLivro.getText().toString().trim() + PDF_EXTENSION;
            File file = new File(filePath);
            OutputStream fileOutputStream = new FileOutputStream(file);

            Document document = new Document();
            PdfWriter.getInstance(document, fileOutputStream);
            document.open();
            document.add(new Paragraph(prompt));
            document.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), "devandroid.george.listacurso", file);
            intent.setDataAndType(uri, APPLICATION_PDF);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        prompt = "Resuma o livro ";

        //}
        //});
        //thread.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissao = true;
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                permissao = false;
                break;
            }
        }
        if (!permissao) {
            Toast.makeText(getBaseContext(), "Aceite os termos", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}