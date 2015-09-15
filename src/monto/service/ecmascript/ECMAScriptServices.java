package monto.service.ecmascript;

import monto.service.MontoService;
import org.apache.commons.cli.*;
import org.zeromq.ZContext;

import java.util.ArrayList;
import java.util.List;

public class ECMAScriptServices {

    public static void main(String[] args) throws ParseException {
        String address = "tcp://*";
        String regAddress = "tcp://*:5004";
        ZContext context = new ZContext(1);
        List<MontoService> services = new ArrayList<>();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("terminating...");
                for (MontoService service : services) {
                    service.stop();
                }
                context.destroy();
                System.out.println("everything terminated, good bye");
            }
        });

        Options options = new Options();
        options.addOption("t", false, "enable ecmascript tokenizer")
                .addOption("p", false, "enable ecmascript parser")
                .addOption("o", false, "enable ecmascript outliner")
                .addOption("c", false, "enable ecmascript code completioner")
                .addOption("f", false, "enable ecmascript FlowType checker")
                .addOption("address", true, "address of services")
                .addOption("registration", true, "address of broker registration");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("address")) {
            address = cmd.getOptionValue("address");
        }

        if (cmd.hasOption("registration")) {
            regAddress = cmd.getOptionValue("registration");
        }


        if (cmd.hasOption("t")) {
            services.add(new ECMAScriptTokenizer(context, address, regAddress, "ecmascriptTokenizer"));
        }
        if (cmd.hasOption("p")) {
            services.add(new ECMAScriptParser(context, address, regAddress, "ecmascriptParser"));
        }
        if (cmd.hasOption("o")) {
            services.add(new ECMAScriptOutliner(context, address, regAddress, "ecmascriptOutliner"));
        }
        if (cmd.hasOption("c")) {
            services.add(new ECMAScriptCodeCompletion(context, address, regAddress, "ecmascriptCodeCompletioner"));
        }
        if (cmd.hasOption("f")) {
            services.add(new ECMAScriptErrorChecker(context, address, regAddress, "ecmascriptErrorChecker"));
        }

        for (MontoService service : services) {
            service.start();
        }
    }
}
