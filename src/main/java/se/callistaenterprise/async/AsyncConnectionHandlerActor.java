package se.callistaenterprise.async;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import scala.concurrent.duration.Duration;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class AsyncConnectionHandlerActor extends UntypedActor {

    private final AsyncContext async;
    private final ServletOutputStream out;

    private Object nextMsg = new Cont(0);

    public AsyncConnectionHandlerActor(final AsyncContext async, ServletOutputStream out) {
        this.async = async;
        this.out = out;

        // register a writer with the output stream
        out.setWriteListener(new WriteListener() {
            ActorRef actor = getSelf();
            @Override
            public void onWritePossible() throws IOException {
                // Send message to actor (self)
                actor.tell(nextMsg, null);
            }

            @Override
            public void onError(Throwable throwable) {
                async.complete();
            }
        });
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof Cont) {

            Cont c = (Cont)message;

            writeAndFlush("<div>Message" + c.cnt + "</div>\n");

            if(c.cnt >= 100) { // We are finished
                // Complete servlet async context
                async.complete();

                // Stop Actor
                getContext().stop(getSelf());
            } else {
                nextMsg = new Cont(c.cnt + 1);
                // Schedule next message
                if(out.isReady()) scheduleNext();
            }
        } else {
            unhandled(message);
        }
    }

    private void writeAndFlush(String msg) throws Exception {
        out.write(msg.getBytes("UTF-8"));
        if(out.isReady()) out.flush();
    }

    private void scheduleNext() {
        getContext()
                .system()
                .scheduler()
                .scheduleOnce(
                        Duration.create(10, TimeUnit.MILLISECONDS),
                        getSelf(),
                        nextMsg,
                        getContext()
                                .system()
                                .dispatcher(),
                        null);
    }

    static final class Cont{
        int cnt = 0;
        Cont(int cnt){
            this.cnt = cnt;
        }
    }
}
