import java.util.Scanner;
import java.io.*;
import java.util.*;
// java Quiz inputFile
// java Quiz sampleInput

// device_category:
//  PulseSensor
//  BloodPressureSensor
//  TemperatureSensor
enum Status{
    falls,
    danger,
    ok
}
class Sensor{
    class data{
        Status s;
        int time;
        float val;
    }

    Vector<data> data_v = new Vector<>();
    int period;
    float lower, upper;
    String name;
    String type;
    String dataset;
    String patient_name;

    

    Sensor(String type, String name, String dataset, float lower, float upper, int period, String patient_name, int monitor_period) throws FileNotFoundException
    {
        this.type = type;
        this.name = name;
        this.dataset = dataset;
        this.lower = lower;
        this.upper = upper;
        this.period = period;
        this.patient_name = patient_name;

        Scanner scanner = new Scanner(new File(dataset));
        int time = 0;
        float val = 0;
        while(scanner.hasNextFloat())
        {
            val = scanner.nextFloat();
            data a = new data();
            a.time = time;
            a.val = val;
            if (val < 0)
            {
                a.s = Status.falls;
            }
            else if(val >= lower && val <= upper)
            {
                a.s = Status.ok;
            }
            else{
                a.s = Status.danger;
            }
            data_v.add(a);
            time += this.period;
        }
        while(time <= monitor_period)
        {
            data a = new data();
            a.time = time;
            a.val = val;
            if (val < 0)
            {
                a.s = Status.falls;
            }
            else if(val >= lower && val <= upper)
            {
                a.s = Status.ok;
            }
            else{
                a.s = Status.danger;
            }
            data_v.add(a);
            time += this.period;
        }
    }
    void check_data(int cur_time, FileWriter o) throws IOException
    {
        for (data x: data_v)
        {
            if (x.time == cur_time)
            {   
                if(x.s == Status.ok)
                    continue;
                else if(x.s == Status.falls)
                {
                    o.write("[" + x.time + "] " + name + " falls\n");
                }
                else if(x.s == Status.danger){
                    o.write( "[" + x.time + "] " + patient_name + " is in danger! Cause: " + name + " " + Float.toString(x.val) + "\n");
                }
                break;
            }
        }
    }
    void print_data(FileWriter o) throws IOException
    {
        o.write(type + " " + name + "\n");
        for (data x: data_v)
        {
            o.write("[" + x.time + "] " + x.val + "\n");
        }

    }

}
class Patient{
    String name;
    int period;
    Vector<Sensor> sensor_v = new Vector<>();
    String dataset;

    Patient(String name, int period)
    {
        this.name = name;
        this.period = period;
    }
    void add_sensor(Sensor s){
        sensor_v.add(s);
    }
    void print_data(FileWriter o) throws IOException{
        o.write("patient " + name + "\n");
        for (Sensor x: sensor_v)
        {
            
            x.print_data(o);
        }
    }
}
class Quiz{
    public static void main(String args[]) throws IOException{
        if(args.length != 1){
            System.out.println("usage: java Quiz sampleInput");
            return;
        }
        Vector<Patient> patients = new Vector<>();
        Patient t_patient;
        String sampleInput = args[0];
        
        FileWriter output = new FileWriter("output");
        // Scanner scanner = new Scanner(System.in);
        // int monitor_period =  scanner.nextInt();
        // System.out.printf("%d", monitor_period);
        // System.out.printf("Hello! %s!", scanner.next());

        // patient {patient_name} {patient_period}

        // {device_category} {device_name} {factor_dataset_file}
        // {safe_range_lower_bound} {safe_range_upper_bound}

        // BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // String line = reader.readLine();

        File sampleFile = new File(sampleInput);
        BufferedReader reader = new BufferedReader(new FileReader(sampleFile));
        String line;
        int monitor_period = Integer.parseInt(reader.readLine());
        
        int period = 0;
        Boolean error = false;
        String patient_name = new String();
        while ( (line = reader.readLine()) != null)
        {
            // read patient
            // System.out.println(line);
            String[] parts = line.split(" ");
            // System.out.println(parts[0]+"***");
            // System.out.println(parts[0] == "patient");
            if(parts[0].equals("patient"))
            {
                error = false;

                patient_name = parts[1];
                period = Integer.parseInt(parts[2]);
                if (period <= 0)
                {
                    output.write("Input Error\n");
                    error = true;
                }
                else{
                    t_patient = new Patient(patient_name, period);
                    patients.add(t_patient);
                }

            }
            else if(error)
            {
                continue;
            }
            else{
                int lower = Integer.parseInt(parts[3]);
                int upper = Integer.parseInt(parts[4]);
                Sensor s = new Sensor(parts[0], parts[1], parts[2], lower, upper, period, patient_name, monitor_period);
                patients.lastElement().add_sensor(s);

            }
            
        }

        
        int time = 0;
        while(time <= monitor_period)
        {
            for (Patient x: patients){
                for (Sensor y: x.sensor_v)
                {
                    y.check_data(time, output);
                }
            }
            time++;
        }

        for (Patient x: patients){
            x.print_data(output);
        }

        // System.out.println(patients.size());
        output.flush();
        output.close();
    }
}