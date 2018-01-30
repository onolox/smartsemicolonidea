import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

/**
 * Created by Fabio on 21/09/2015.
 */
public class SSemicolon extends TypedHandlerDelegate {
    protected static Boolean pontoVirgula = false;
    protected static int linhaPontoVirgula, colunaAtual;

    public Result beforeCharTyped(char c, Project project, final Editor editor, PsiFile file, FileType fileType) {
        Result result = Result.CONTINUE;
        String extension = fileType.getDefaultExtension();

        if (extension.equals("java") || extension.equals("js")) {
            if (c == ';') {
                final Document document = editor.getDocument();
                final CaretModel caretModel = editor.getCaretModel();
                final Caret caret = caretModel.getCurrentCaret();
                result = Result.STOP;

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        int posicaoInicial = caret.getOffset();
                        int linhaAtual = caretModel.getVisualPosition().getLine();
                        int colunaAtual = caretModel.getVisualPosition().getColumn();

                        if (linhaPontoVirgula != linhaAtual) { // Checa se a linha atual da inserção é a mesma, caso não seja cancela todas ações
                            pontoVirgula = false;
                            linhaPontoVirgula = linhaAtual;
                        }
                        if (colunaAtual != SSemicolon.this.colunaAtual) { // Checa se é a mesma coluna
                            pontoVirgula = false;
                            SSemicolon.this.colunaAtual = colunaAtual;
                        }

                        caret.setSelection(caret.getVisualLineEnd() - 2, caret.getVisualLineEnd() - 1);

                        if (!caret.getSelectedText().contains(";")) {        // não tem ;
                            caret.setSelection(colunaAtual, colunaAtual);
                            caret.moveToOffset(caret.getVisualLineEnd() - 1);
                            document.insertString(caret.getOffset(), ";");
                            if (caret.getVisualLineEnd() - 2 == posicaoInicial)
                                caret.moveToVisualPosition(new VisualPosition(caret.getVisualPosition().line, caret.getVisualLineEnd()));
                            else caret.moveToOffset(posicaoInicial);
                            pontoVirgula = true;
                        }
                        else {
                            caret.setSelection(colunaAtual, colunaAtual);
                            caret.moveToOffset(posicaoInicial);
                            document.insertString(caret.getOffset(), ";");
                            caret.moveToOffset(posicaoInicial + 1);
                            pontoVirgula = false;
                        }
                    }
                };
                runnable.run();
            }
        }
        else pontoVirgula = false;

        return result;
    }
}
