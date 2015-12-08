package me.zzp.jss;

import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 语法：
 * <dl>
 *     <dt><code>[! !]</code></dt>
 *     <dd>代码</dd>
 *     <dt><code>[= =]</code></dt>
 *     <dd>表达式</dd>
 * </dl>
 */
class ScriptInputStream extends InputStream {

    private static enum State {
        Plain,
        Script,
        Expression,
        SingleQuoteString,
        DoubleQuoteString
    }

    private final static String PLAIN_PREFIX = "response.print('";
    private final static String PLAIN_SUFFIX = "');\n";
    private final static String EXPRESSION_PREFIX = "response.print(";
    private final static String EXPRESSION_SUFFIX = ");\n";

    private final InputStream source;
    private final Deque<Integer> queue;
    private State last;
    private State state;
    private boolean finished;

    ScriptInputStream(InputStream source) {
        this.source = source;
        queue = new LinkedList<>();
        start();
    }

    /* append */

    private void append(String content) {
        for (int value : content.getBytes()) {
            append(value);
        }
    }

    private void append(int value) {
        queue.offerLast(value);
    }

    /* state machine */

    private void prefix() {
        if (state == State.Plain) {
            append(PLAIN_PREFIX);
        } else if (state == State.Expression) {
            append(EXPRESSION_PREFIX);
        }
    }

    private void suffix() {
        if (state == State.Plain) {
            append(PLAIN_SUFFIX);
        } else if (state == State.Expression) {
            append(EXPRESSION_SUFFIX);
        }
    }

    private void transfer(State state) {
        if (this.state == state) {
            return;
        }

        suffix();
        last = this.state;
        this.state = state;
        prefix();
    }

    private void restore() {
        state = last;
        last = null;
    }

    private void start() {
        transfer(State.Plain);
        finished = false;
    }

    private void end() {
        suffix();
        finished = true;
    }

    /**
     * 只管输入，不管语法检查。
     * @return
     * @throws IOException
     */
    private int next() throws IOException {
        if (finished) {
            return -1;
        }

        int value = source.read();
        if (value < 0) {
            end();
            return queue.isEmpty()? -1: queue.pollFirst();
        }

        switch (state) {
            case Plain: {
                switch (value) {
                    case '\b': {
                        append("\\b");
                    } break;
                    case '\f': {
                        append("\\f");
                    } break;
                    case '\n': {
                        append("\\n");
                    } break;
                    case '\r': {
                        append("\\r");
                    } break;
                    case '\t': {
                        append("\\t");
                    } break;
                    case '\'': {
                        append("\\'");
                    } break;
                    case '[': {
                        value = source.read();
                        if (value == '!') {
                            transfer(State.Script);
                        } else if (value == '=') {
                            transfer(State.Expression);
                        } else {
                            append('[');
                            append(value);
                        }
                    } break;
                    default: {
                        append(value);
                    } break;
                }
            } break;

            case Script:
            case Expression: {
                append(value);
                if (value == '\'') {
                    transfer(State.SingleQuoteString);
                } else if (value == '"') {
                    transfer(State.DoubleQuoteString);
                } else if ((state == State.Script && value == '!') || (state == State.Expression && value == '=')) {
                    value = source.read();
                    if (value == ']') {
                        queue.pollLast();
                        transfer(State.Plain);
                    } else {
                        append(value);
                    }
                }
            } break;

            case SingleQuoteString:
            case DoubleQuoteString: {
                append(value);
                if ((state == State.SingleQuoteString && value == '\'') || (state == State.DoubleQuoteString && value == '"')) {
                    restore();
                } else if (value == '\\') {
                    value = source.read();
                    if (value >= 0) {
                        append(value);
                    }
                }
            } break;
        }

        return queue.pollFirst();
    }

    @Override
    public int read() throws IOException {
        return queue.isEmpty()? next(): queue.pollFirst();
    }
}
