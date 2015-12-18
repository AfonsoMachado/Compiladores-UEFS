while (estruturaLexica.ehOperador(ch)) {
            this.coluna++;
            if (lexema.equals(".")) {
                error = true;
                lexema += ch;
            } else if (lexema.equals("+")) {
                if (ch != '+') {
                    error = true;
                }
                lexema += ch;
            } else if (lexema.equals("-")) {
                if (ch != '-') {
                    error = true;
                }
                lexema += ch;
            } else if (lexema.equals("*")) {
                error = true;
                lexema += ch;
            } else if (lexema.equals("/")) {
                if (ch == '/' || ch == '*') {
                    //lexema.substring(0, lexema.length()-1);
                    //this.comentario(ch);
                } else {
                    error = true;
                }
            } else if (!lexema.equals("/") && (ch == '/' || ch == '*')) {
                //ch = novoChar();
                //lexema.substring(0, lexema.length()-1);
                //this.comentario(ch);

            } else if (lexema.equals("=") || lexema.equals("!") || lexema.equals("<") || lexema.equals(">")) {
                if (ch != '=') {
                    error = true;
                }
                lexema += ch;
            } else if (lexema.equals("&")) {
                if (ch != '&') {
                    error = true;
                }
                lexema += ch;
            } else if (lexema.equals("|")) {
                if (ch != '|') {
                    error = true;
                }
                lexema += ch;
            }

            ch = this.novoChar();
        }

        if (estruturaLexica.ehLetra(ch) && lexema.equals("-")) {
            this.numero(lexema, ch);
            return;
        }