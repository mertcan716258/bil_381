import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.misc.*;

import javax.xml.transform.stream.StreamSource;
import java.util.*;

public class CallGraphListener extends Java8BaseListener {

    int sayac = 1;
    public Map <String , ArrayList<String>> methodlar = new HashMap<>();
    public static String method_ismi;
    public static String class_ismi;
    public static String package_ismi;

    public void enterMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        method_ismi = package_ismi + "/" + class_ismi +"/"+ctx.methodHeader().methodDeclarator().Identifier()+"";
        if(!methodlar.containsKey(method_ismi)) {
            methodlar.put(method_ismi, new ArrayList<String>());
        }
    }
    public void enterNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        class_ismi = ctx.Identifier()+"";
    }
    public void enterPackageDeclaration(Java8Parser.PackageDeclarationContext ctx){
        package_ismi = ctx.Identifier()+"";
    }

    public void enterExpressionStatement(Java8Parser.ExpressionStatementContext ctx) {
        String cagrilan_method = "";
        if (methodlar.containsKey(method_ismi)) {
            if (ctx.statementExpression().methodInvocation().getChildCount() != 3) {

                cagrilan_method = package_ismi + "/" + ctx.statementExpression().methodInvocation().getChild(0).getText() +
                        "/" + ctx.statementExpression().methodInvocation().getChild(2);
            } else {
                cagrilan_method = package_ismi + "/" + class_ismi + "/" + ctx.statementExpression().methodInvocation().getChild(0).getText();
            }
            ArrayList<String> list = methodlar.get(method_ismi);
            list.add(cagrilan_method);
            methodlar.put(method_ismi, list);

        }
        else {
            if (ctx.statementExpression().methodInvocation().getChildCount() != 3) {
                cagrilan_method = package_ismi + "/" + ctx.statementExpression().methodInvocation().getChild(0).getText() +
                        "/" + ctx.statementExpression().methodInvocation().getChild(2);
            } else {
                cagrilan_method = package_ismi + "/" + class_ismi + "/" + ctx.statementExpression().methodInvocation().getChild(0).getText();
            }
            ArrayList<String> list = new ArrayList<>();
            list.add(cagrilan_method);
            methodlar.put(method_ismi, list);
        }
    }

    public static void main(String[] args) throws Exception {
        ANTLRInputStream input = new ANTLRInputStream(System.in);
        Java8Lexer lexer = new Java8Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        CallGraphListener listener = new CallGraphListener();
        // This is where we trigger the walk of the tree using our listener.

        walker.walk(listener, tree);

        StringBuilder buf = new StringBuilder();
        buf.append("digraph G {\n");

        Iterator hmIterator_2 = listener.methodlar.entrySet().iterator();

        while (hmIterator_2.hasNext()) {
            Map.Entry mapElement = (Map.Entry)hmIterator_2.next();
            String key = (String)mapElement.getKey();
            buf.append(String.format("\"%s\"[color = green];\n", key));
        }

        Iterator hmIterator = listener.methodlar.entrySet().iterator();

        String caller_method_name = "";

        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)hmIterator.next();

            ArrayList <String> list_2 = (ArrayList<String>)mapElement.getValue();
            caller_method_name = (String)mapElement.getKey();

            for (int i=0 ; i<list_2.size() ; i++) {
                buf.append(String.format("\"%s\" -> \"%s\";\n", caller_method_name, list_2.get(i)));
            }
        }

        buf.append("}");
        System.out.println(buf.toString());
    }

}

