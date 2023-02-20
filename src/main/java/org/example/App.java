package org.example;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import static java.util.Arrays.asList;

import java.util.Iterator;
import java.util.Scanner;

public class App {

    //CONFIGURACION DE LA CONEXION DE LA BASE DE DATOS
    private MongoClient mongoClient;
    private MongoDatabase mongodb;
    public void connectDatabase(){
        setMongoClient(new MongoClient());
        setMongodb(getMongoClient().getDatabase("HitoGrupal"));

    }

    //Getter y setter
    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public MongoDatabase getMongodb() {
        return mongodb;
    }

    public void setMongodb(MongoDatabase mongodb) {
        this.mongodb = mongodb;
    }

    //METODO INSERT CLIENTE
    public void insertOneDataTest( String nombre,String apellidos,String numeroTelefono,String fecha,String motivo,String tipo,String reparacion,String solucion){
        getMongodb().getCollection("cliente").insertOne(
                new Document()
                        .append("nombre", nombre)
                        .append("apellidos", apellidos)
                        .append("telefono", numeroTelefono)
                        .append("historial", asList(
                                new Document()
                                        .append("Fecha",fecha)
                                        .append("motivo", motivo)
                                        .append("TipoProblema", tipo)
                                        .append("reparacion", reparacion.toUpperCase())
                                        .append("Solucionado", solucion.toUpperCase())
                                ))
                        );
    }

    //METODO LISTAR CLIENTES
    public void listarClientes() {
        MongoCollection<Document> collection = getMongodb().getCollection("cliente");
        FindIterable<Document> iterDoc = collection.find();
        int i = 1;
        Iterator it = iterDoc.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
            i++;
        }
    }

    //METODO PARA ELIMINAR CLIENTE
    public void eliminarCliente(String borrar){
        MongoCollection<Document> collection = getMongodb().getCollection("cliente");
        collection.deleteOne(new Document("_id", new ObjectId(borrar)));
        System.out.println("Borrado correctamente...");
        //Retrieving the documents after the delete operation
        FindIterable<Document> iterDoc = collection.find();
        int i = 1;
        System.out.println("Los registros restantes son:");
        Iterator it = iterDoc.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
            i++;
        }
    }

    //METODO PARA ACTUALIZAR CLIENTE
    public void actualizarCliente(String id,String nombre,String apellidos,String numeroTelefono,String fecha,String motivo,String tipo,String reparacion,String solucion){
        MongoCollection<Document> collection = getMongodb().getCollection("cliente");
        collection.updateOne(new Document("_id", new ObjectId(id)),
                new Document("$set", new Document("nombre", nombre)
                        .append("apellidos", apellidos)
                        .append("telefono", numeroTelefono)
                        .append("historial", asList(
                                new Document()
                                        .append("Fecha",fecha)
                                        .append("motivo", motivo)
                                        .append("TipoProblema", tipo)
                                        .append("reparacion", reparacion)
                                        .append("Solucionado", solucion)
                        ))));
        System.out.println("Actualizado correctamente...");

        //Recibir los documentos después de la actualización
        FindIterable<Document> iterDoc = collection.find();
        int i = 1;
        System.out.println("Los registros restantes son:");
        Iterator it = iterDoc.iterator();

        while (it.hasNext()) {
            System.out.println(it.next());
            i++;
        }
    }

    //METODO PARA ENCONTRAR REGISTROS CON HARDWARE O SOFTWARE
    public void llamadasHardware(String problema){
        MongoCollection<Document> collection = getMongodb().getCollection("cliente");

        //Crear documento
        Document findDocument = new Document("historial.TipoProblema", problema);

        //Documento que almacena los resultados
        MongoCursor<Document> resultDocument = collection.find(findDocument).iterator();
        System.out.println("*** Listado Problemas de : " + problema + " ***");

        //Iterar sobre los resultados
        int cont=0;
        while (resultDocument.hasNext()) {
            cont++;
            System.out.println(resultDocument.next());
        }
        System.out.println("Numero de llamadas de "+problema+" : " + cont);
    }

    //MÉTODO PARA MOSTRAR REPARACIONES
    public void llamadasReparacion(String reparacion1){
        MongoCollection<Document> collection = getMongodb().getCollection("cliente");

        //Crear documentos
        Document findDocument = new Document("historial.reparacion", reparacion1);

        //Documento que almacena los resultados
        MongoCursor<Document> resultDocument = collection.find(findDocument).iterator();
        System.out.println("*** Listado reparaciones de : " + reparacion1 + " ***");

        //Iterar sobre los resultados
        while (resultDocument.hasNext()) {
            System.out.println(resultDocument.next());
        }
    }

    //MÉTODO PARA MOSTRAR SOLUCIONES
    public void llamadasSolucion(String solucion1){
        MongoCollection<Document> collection = getMongodb().getCollection("cliente");

        //Crear el documento
        Document findDocument = new Document("historial.Solucionado", solucion1);

        //Documento que almacena los resultados
        MongoCursor<Document> resultDocument = collection.find(findDocument).iterator();
        System.out.println("*** Listado reparaciones de : " + solucion1 + " ***");

        //Iterar sobre los resultados
        while (resultDocument.hasNext()) {
            System.out.println(resultDocument.next());
        }
    }

    //EMPEZAMOS CON LA LOGICA
    public static void main(String args[]){
        //PRIMERO CONECTAMOS
        App app = new App ();
        app.connectDatabase();

        //AHORA HACEMOS EL MENU DE OPCIONES
        System.out.println("Seleccione una opcion");
        System.out.println("1º Dar de Alta un cliente");
        System.out.println("2º Mostrar todos los clientes");
        System.out.println("3º Eliminar un cliente");
        System.out.println("4º Actualizar un cliente");
        System.out.println("5º Cuantas llamadas han sido de Hardware y Software");
        System.out.println("6º Cuantas llamadas necesitan reparacion");
        System.out.println("7º Cuantas llamadas han sido solucionadas");

        Scanner lector = new Scanner(System.in);
        String opcion= lector.nextLine();

        //MENÚ DE OPCIONES
        switch (opcion){

            //Insertar clientes
            case "1":
                System.out.println("Ha seleccionado dar de alta un cliente");
                System.out.println("Inserte el nombre del cliente");
                String nombreCliente = lector.nextLine();
                System.out.println("Inserte el Apellidos del cliente");
                String apellidoscliente = lector.nextLine();
                System.out.println("Inserte el Telefono del cliente");
                String telefonocliente = lector.nextLine();
                System.out.println("Vamos a proceder a rellenar datos de su historial");
                System.out.println("Inserte la fecha de hoy en este formato");
                String fechacliente = lector.nextLine();
                System.out.println("Inserte el motivo del cliente");
                String motivocliente = lector.nextLine();
                System.out.println("Tipo de problema");
                String tipoProblema = lector.nextLine();
                System.out.println("Reparacion:SI/NO");
                String reparacioncliente = lector.nextLine();
                System.out.println("Solucion:SI/NO");
                String solucioncliente= lector.nextLine();
                app.insertOneDataTest(nombreCliente,apellidoscliente,telefonocliente,fechacliente,motivocliente,tipoProblema,reparacioncliente,solucioncliente);
                break;

            //Mostrar clientes
            case "2":
                app.listarClientes();
                break;

            //Eliminar clientes
            case "3":
                System.out.println("Seleccione un id a eliminar entre los siguientes");
                app.listarClientes();
                String ABorrar = lector.nextLine();
                app.eliminarCliente(ABorrar);
                break;

                //Actualizar clientes
            case "4":
                System.out.println("Seleccione un id a actualizar entre los siguientes");
                app.listarClientes();
                String id = lector.nextLine();
                System.out.println("Inserte el nombre del cliente");
                String nombre = lector.nextLine();
                System.out.println("Inserte el Apellidos del cliente");
                String apellidos = lector.nextLine();
                System.out.println("Inserte el Telefono del cliente");
                String numeroTelefono = lector.nextLine();
                System.out.println("Vamos a proceder a rellenar datos de su historial");
                System.out.println("Inserte la fecha de hoy en este formato");
                String fecha = lector.nextLine();
                System.out.println("Inserte el motivo del cliente");
                String motivo = lector.nextLine();
                System.out.println("Tipo de problema");
                String tipo = lector.nextLine();
                System.out.println("Reparacion:SI/NO");
                String reparacion = lector.nextLine();
                System.out.println("Solucion:SI/NO");
                String solucion = lector.nextLine();

                app.actualizarCliente(id,nombre,apellidos,numeroTelefono,fecha,motivo,tipo,reparacion,solucion);
                break;

                //Mostrar registros de hardware o software
            case "5":
                System.out.println("Inserte el tipo de problema. Ejemplo: Hardware o Software");
                Scanner lector2 = new Scanner(System.in);
                String problema = lector2.nextLine();
                app.llamadasHardware(problema);
                break;

            //Mostrar registros con tipo de reparación
            case "6":
                System.out.println("Inserte el tipo reparacion. Ejemplo: SI o NO");
                Scanner lector3 = new Scanner(System.in);
                String reparacion1 = lector3.nextLine();
                app.llamadasReparacion(reparacion1.toUpperCase());
                break;

            //Mostrar informes de solución
            case "7":
                System.out.println("Inserte si se ha solucionado. Ejemplo: SI o NO");
                Scanner lector4 = new Scanner(System.in);
                String solucion1 = lector4.nextLine();
                app.llamadasSolucion(solucion1.toUpperCase());
                break;

                //Opcion por defecto
            default:
                System.out.println("No se ha ecogido ninguna opcion");
                break;
        }
    }
}