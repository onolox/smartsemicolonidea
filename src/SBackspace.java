import com.intellij.codeInsight.editorActions.BackspaceHandlerDelegate;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.playback.commands.ActionCommand;
import com.intellij.psi.PsiFile;

public class SBackspace extends BackspaceHandlerDelegate {
    private boolean charDeleted = false;

    @Override
    public void beforeCharDeleted(char c, PsiFile psiFile, Editor editor) {
        if (SSemicolon.pontoVirgula)                // Se clicar backspace o ponto e virgula volta a posição inicial do carret
        {
            final Document document = editor.getDocument();
            final CaretModel caretModel = editor.getCaretModel();
            final Caret caret = caretModel.getCurrentCaret();

            if ((caretModel.getVisualPosition().getLine() == SSemicolon.linhaPontoVirgula) && (caretModel.getVisualPosition().getColumn() == SSemicolon.colunaAtual)) {

                ActionManager actionManager = ActionManager.getInstance();
                AnAction actionHandler = actionManager.getAction(IdeActions.ACTION_UNDO);
                actionManager.tryToExecute(actionHandler, ActionCommand.getInputEvent(IdeActions.ACTION_UNDO), editor.getComponent(), null, true);

                document.insertString(caret.getOffset(), ";" + c);
                caret.moveToOffset(caret.getOffset() + 2);
            }
            charDeleted = false;
            SSemicolon.pontoVirgula = false;
        }
        else {
            charDeleted = true;
        }
    }

    @Override
    public boolean charDeleted(char c, PsiFile psiFile, Editor editor) {
        return charDeleted;
    }
}
