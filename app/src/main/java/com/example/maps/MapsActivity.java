package com.example.maps;

import static java.lang.Boolean.TRUE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.clustering.ClusterManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

// Implement OnMapReadyCallback.
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseDatabase EdificiosDatabase;
    private DatabaseReference ListaDeEdificios;
    private float hora, minutos, segundos;
    private String dayName;
    private ArrayList<Marker> Salones = new ArrayList<>();
    TextView tvPrueba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout file as the content view.
        setContentView(R.layout.activity_maps);
        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        EdificiosDatabase = FirebaseDatabase.getInstance();
        ListaDeEdificios = EdificiosDatabase.getReference();

        //center = mMap.addMarker(mMarker);
        //refresh(1000);
        //content();
        //countDownTimer();
    }
    /*public void content(){
        //onMapReady(mMap);
        refresh(1000);
    }
    private void refresh(int miliseconds){
        final Handler mHandler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Calendar calendario = Calendar.getInstance();
                hora = calendario.get(Calendar.HOUR_OF_DAY);
                minutos = calendario.get(Calendar.MINUTE);
                segundos = calendario.get(Calendar.SECOND);
                if(segundos >= 30) {
                    mMap.clear();
                    onMapReady(mMap);
                    //content();
                }
                content();
            }
        };
        mHandler.postDelayed(runnable,miliseconds);
    }*/

    /*private void countDownTimer(){
        new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.e("seconds remaining:","" + millisUntilFinished / 1000);
            }
            public void onFinish() {
                Toast.makeText(MapsActivity.this,
                                "Puntos Actualizados",
                                    Toast.LENGTH_SHORT).show();
                mMap.clear();
                onMapReady(mMap);
            }
        }.start();
    }*/
    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ArrayList<Polygon> polygonSalon_O = new ArrayList<>();
        ArrayList<String> Edificios = new ArrayList<>();
        Date x = new Date();
        int dayNumber = x.getDay();

        //Almacena el dia en el que estamos
        //WARNING : Si el dia en el que estamos es Sabado o Domingo
        //crasheara la app ... creo
        //Obra en proceso para arreglar eso
        getDayName(dayNumber);
        Log.d("Dia", dayName);

        AddFloor1();
        Button btnP3 = findViewById(R.id.btnP3);
        Button btnP2 = findViewById(R.id.btnP2);
        Button btnP1 = findViewById(R.id.btnP1);
        btnP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Piso1", Toast.LENGTH_SHORT).show();
                AddFloor1();
            }});
        btnP2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Piso2", Toast.LENGTH_SHORT).show();
                AddFloor2();
            }});
        btnP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Piso3",Toast.LENGTH_SHORT).show();
                AddFloor3();
            }
        });
        //Restringe el desplazamiento lateral del usuario en un área determinada
        LatLngBounds engineeringBounds = new LatLngBounds(
                new LatLng(19.164345108507927, -96.11503944143145), // SW bounds
                new LatLng(19.166327605433096, -96.11318303485193)  // NE bounds
        );
        // Constrain the camera target to the Adelaide bounds.
        mMap.setLatLngBoundsForCameraTarget(engineeringBounds);

        mMap.setMinZoomPreference(18.5f);    //Establecer Limites de zoom Minmimos
        mMap.setMaxZoomPreference(21f);      //Establecer Limites de zoom Maximos

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                BottomSheetDialog dialog = new BottomSheetDialog(MapsActivity.this);
                View vista = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottomsheet_layout, null);
                tvPrueba = vista.findViewById(R.id.tvPrueba);
                tvPrueba.setText(marker.getTitle());
                dialog.setCancelable(true);
                dialog.setContentView(vista);
                dialog.show();
                    return false;
                }
                });


    }
    private void getDayName(int numberDay){
        switch (numberDay){
            case 1: dayName = "Lunes";
                break;
            case 2: dayName = "Martes";
                break;
            case 3: dayName = "Miercoles";
                break;
            case 4: dayName = "Jueves";
                break;
            case 5: dayName = "Viernes";
                break;
            default: dayName = "";
                break;
        }
    }
    void RefreshHorarios(ArrayList<Marker> TempSalones, ArrayList<GroundOverlay> TempSalonColor){
        ArrayList<Marker> Salones = TempSalones;
        ArrayList<GroundOverlay> salonColor = TempSalonColor;
        Calendar calendario = Calendar.getInstance();
        hora = calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE);
        segundos = calendario.get(Calendar.SECOND);


        //Log.d("Salon", (String) Salones.get(0).getTag());
        //Proceso de busqueda de edificios y salones y asignacion de horarios
        ListaDeEdificios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Log.d("KEY", snapshot1.getKey());
                    String NameEdificio = snapshot1.getKey();
                    ListaDeEdificios.child(NameEdificio).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.d("Edificio:", snapshot.getKey() + " - " + snapshot.getValue().toString() + "\n");
                                ClassRooms s = snapshot.getValue(ClassRooms.class);
                                Log.d("EE ", s.EE + " Salon " + s.Salon);
                                //---------------Modificar para ponerlo al dia----------------
                                String base = NameEdificio+"/" + snapshot.getKey() + "/"+dayName;
                                Log.d("BASE ", base);
                                ListaDeEdificios.child(base).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Log.d("EE",s.EE);
                                        Log.d("Disp", snapshot.getValue().toString());
                                        s.dia = snapshot.getValue(DIA.class);
                                        Log.d("Disp con objeto", String.valueOf(s.dia.Disponibilidad));
                                        Log.d("Hora_Entrada", String.valueOf(s.dia.Hora_Entrada));
                                        Log.d("Hora_Salida", String.valueOf(s.dia.Hora_Salida));
                                        //Verificar si "Disponibilidad" es VERDADERO
                                        //Verificar si hay clases
                                        if (s.dia.Disponibilidad == TRUE) {
                                            Log.d("SI HAY CLASES", "ABIERTO");
                                            //Si hay clases, ver si esta en el rango de la hora
                                            if (hora >= s.dia.Hora_Entrada && hora < s.dia.Hora_Salida) {
                                                Log.d("Esta dentro de rango de clases", "Clase de: " + s.EE);
                                                //Recorrer cada salon que hay agregado
                                                for (int i = 0; i < Salones.size(); i++) {
                                                    String databaseSalon = s.Salon;
                                                    String arraylistSalon = String.valueOf(Salones.get(i).getTag());
                                                    Log.d("Database Salon", databaseSalon);
                                                    Log.d("ArrayList", arraylistSalon);
                                                    //Buscar el salon correspondiente con la materia que le toca
                                                    //por medio de los tags que tienen los salones
                                                    if (Objects.equals(databaseSalon, arraylistSalon)) {
                                                        Log.d("COINCIDENCIA?", "SI LO CREO");
                                                        int Hora12 = calendario.get(Calendar.HOUR);
                                                        Salones.get(i).setTitle(s.EE);
                                                        String Horario = s.dia.Hora_Entrada + " - " + s.dia.Hora_Salida;
                                                        Salones.get(i).setSnippet(Horario);
                                                        //polygonSalon_O.get(i).setFillColor(Color.GREEN);
                                                        salonColor.get(i).setImage(BitmapDescriptorFactory.fromResource(R.drawable.open));
                                                    }
                                                }
                                            } else {
                                                Log.d("No esta en el rango de clases", "No clases");
                                            }
                                        } else {
                                            Log.d("NO HAY CLASES", "CERRADO");
                                        }
                                        //Ver que salones no tienen asignado una materia en el rango de hora
                                        for (int i = 0; i < Salones.size(); i++) {
                                            //Log.d("Txt", String.valueOf(Salones_O.get(i).getTitle()));
                                            if (Salones.get(i).getTitle() == null) {
                                                //polygonSalon_O.get(i).setFillColor(Color.RED);
                                                salonColor.get(i).setImage(BitmapDescriptorFactory.fromResource(R.drawable.close));
                                            }
                                        }
                                        //setUpClusterer();
                                        //addItems(Salones_O);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    void AddFloor3(){
        mMap.clear();
        //Marcadores de los salones I
        Marker Salon_I_25 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.1651766473747486815, -96.114465796186761815))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_I_24 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.1652104684658128635, -96.114533146899503635))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_I_23 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.1652442895568770455, -96.114600497612245455))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_I_22 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.16527811064794122725, -96.1146678483249872725))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_I_21 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.165311931739005409, -96.11473519903772909))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        //Dibujo del salon I
        Polygon PolygonSalonIEscaleras = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165189166759674,-96.11437111633568),             //NE
                        new LatLng(19.165206211872836545,-96.11440483778739636),    //NO
                        new LatLng(19.165113261785596636,-96.11445940387338545),    //SO
                        new LatLng(19.165096485807695, -96.11442577461236))         //SE
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_25 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165206211872836545,-96.11440483778739636),
                        new LatLng(19.165240302099161636,-96.11447228069082909),
                        new LatLng(19.165146813741399909,-96.11452666239543636),
                        new LatLng(19.165113261785596636,-96.11445940387338545))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_24 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165240302099161636,-96.11447228069082909),
                        new LatLng(19.165274392325486727, -96.11453972359426182),
                        new LatLng(19.165180365697203182	,-96.11459392091748727),
                        new LatLng(19.165146813741399909,-96.11452666239543636))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_23 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165274392325486727, -96.11453972359426182),
                        new LatLng(19.165308482551811818,-96.11460716649769455),
                        new LatLng(19.165213917653006455	,-96.11466117943953818),
                        new LatLng(19.165180365697203182	,-96.11459392091748727))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_22 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165308482551811818,-96.11460716649769455),
                        new LatLng(19.165342572778136909,-96.11467460940112727),
                        new LatLng(19.165247469608809727,-96.11472843796158909),
                        new LatLng(19.165213917653006455,-96.11466117943953818))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_21 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165342572778136909,-96.11467460940112727),
                        new LatLng(19.165376663004462, -96.11474205230456),
                        new LatLng(19.165281021564613, -96.11479569648364),
                        new LatLng(19.165247469608809727,-96.11472843796158909))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));

        //Coloreado de los Salones I
        GroundOverlay salonColorI_25 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.1651766473747486815, -96.114465796186761815), 8f, 12f));
        GroundOverlay salonColorI_24 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.1652104684658128635, -96.114533146899503635), 8f, 12f));
        GroundOverlay salonColorI_23 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.1652442895568770455, -96.114600497612245455), 8f, 12f));
        GroundOverlay salonColorI_22 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.16527811064794122725, -96.1146678483249872725), 8f, 12f));
        GroundOverlay salonColorI_21 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.165311931739005409, -96.11473519903772909), 8f, 12f));
        GroundOverlay escaleraI_20 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.up))
                .bearing(29f)
                .position(new LatLng(19.165152003003467, -96.11441556126738), 4f, 8f));



        ArrayList<GroundOverlay> TempSalonColor = new ArrayList<>();
        ArrayList<Marker> TempSalones = new ArrayList<>();

        //Introducir tag a los salones I para poder cambiarles de color
        salonColorI_25.setTag("I_25");
        salonColorI_24.setTag("I_24");
        salonColorI_23.setTag("I_23");
        salonColorI_22.setTag("I_22");
        salonColorI_21.setTag("I_21");

        TempSalonColor.add(salonColorI_25);
        TempSalonColor.add(salonColorI_24);
        TempSalonColor.add(salonColorI_23);
        TempSalonColor.add(salonColorI_22);
        TempSalonColor.add(salonColorI_21);

        //Agregando los tags de los salones I
        Salon_I_25.setTag("I_25");
        Salon_I_24.setTag("I_24");
        Salon_I_23.setTag("I_23");
        Salon_I_22.setTag("I_22");
        Salon_I_21.setTag("I_21");

        //Agregando los salones I al array de salones
        TempSalones.add(Salon_I_25);
        TempSalones.add(Salon_I_24);
        TempSalones.add(Salon_I_23);
        TempSalones.add(Salon_I_22);
        TempSalones.add(Salon_I_21);

        RefreshHorarios(TempSalones, TempSalonColor);
    }

    void AddFloor2(){
        mMap.clear();

        //Dibujo del Salon L
        Polygon PolygonSalonL_05A = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165031765661, -96.11445371625378),
                        new LatLng(19.165045797637226308, -96.11448172470657),
                        new LatLng(19.164945235065934462, -96.11453743212646154),
                        new LatLng(19.164931056914792, -96.11450937209277))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonL_05B = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165045797637226308, -96.11448172470657),
                        new LatLng(19.165059829613452615, -96.11450973315936),
                        new LatLng(19.164959413217076923, -96.11456549216015308),
                        new LatLng(19.164945235065934462, -96.11453743212646154))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonL_06 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165059829613452615, -96.11450973315936),
                        new LatLng(19.165087893565905231, -96.11456575006494),
                        new LatLng(19.164987769519361846, -96.11462161222753615),
                        new LatLng(19.164959413217076923, -96.11456549216015308))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonL_07 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165087893565905231, -96.11456575006494),
                        new LatLng(19.165115957518357846, -96.11462176697052),
                        new LatLng(19.165016125821646769, -96.11467773229491923),
                        new LatLng(19.164987769519361846, -96.11462161222753615))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonLEscaleras = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165115957518357846, -96.11462176697052),
                        new LatLng(19.165129989494584154, -96.11464977542331),
                        new LatLng(19.165030303972789231, -96.11470579232861077),
                        new LatLng(19.165016125821646769, -96.11467773229491923))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonL_08 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165129989494584154, -96.11464977542331),
                        new LatLng(19.165158053447036769, -96.11470579232889),
                        new LatLng(19.165058660275074154, -96.11476191239599385),
                        new LatLng(19.165030303972789231, -96.11470579232861077))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonL_09 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165158053447036769, -96.11470579232889),
                        new LatLng(19.165186117399489385, -96.11476180923447),
                        new LatLng(19.165087016577359077, -96.11481803246337692),
                        new LatLng(19.165058660275074154, -96.11476191239599385))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonL_10 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165186117399489385, -96.11476180923447),
                        new LatLng(19.165214181351942, -96.11481782614005),
                        new LatLng(19.165115372879644, -96.11487415253076),
                        new LatLng(19.165087016577359077, -96.11481803246337692))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));

        //Marcadores de los salones I
        Marker Salon_I_11 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.1651766473747486815, -96.114465796186761815))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_I_12 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.1652104684658128635, -96.114533146899503635))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_I_13 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.1652442895568770455, -96.114600497612245455))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_I_14 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.16527811064794122725, -96.1146678483249872725))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_I_15 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.165311931739005409, -96.11473519903772909))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        //Dibujo del salon I
        Polygon PolygonSalonIEscaleras = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165189166759674,-96.11437111633568),
                        new LatLng(19.165206211872836545,-96.11440483778739636),
                        new LatLng(19.165113261785596636,-96.11445940387338545),
                        new LatLng(19.165096485807695, -96.11442577461236))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_11 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165206211872836545,-96.11440483778739636),
                        new LatLng(19.165240302099161636,-96.11447228069082909),
                        new LatLng(19.165146813741399909,-96.11452666239543636),
                        new LatLng(19.165113261785596636,-96.11445940387338545))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_12 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165240302099161636,-96.11447228069082909),
                        new LatLng(19.165274392325486727, -96.11453972359426182),
                        new LatLng(19.165180365697203182	,-96.11459392091748727),
                        new LatLng(19.165146813741399909,-96.11452666239543636))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_13 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165274392325486727, -96.11453972359426182),
                        new LatLng(19.165308482551811818,-96.11460716649769455),
                        new LatLng(19.165213917653006455	,-96.11466117943953818),
                        new LatLng(19.165180365697203182	,-96.11459392091748727))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_14 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165308482551811818,-96.11460716649769455),
                        new LatLng(19.165342572778136909,-96.11467460940112727),
                        new LatLng(19.165247469608809727,-96.11472843796158909),
                        new LatLng(19.165213917653006455,-96.11466117943953818))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_15 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165342572778136909,-96.11467460940112727),
                        new LatLng(19.165376663004462, -96.11474205230456),
                        new LatLng(19.165281021564613, -96.11479569648364),
                        new LatLng(19.165247469608809727,-96.11472843796158909))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));

        //Marcadores de los salones L
        Marker Salon_L_05A = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.1649884638197381925, -96.114495561294895385))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_L_05B = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.165002568883422577, -96.114523595538136155))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_L_06 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.16502372647894915375, -96.1145656469029973075))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_L_07 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.165051936606317923, -96.114621715389478845))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_L_08 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.165094251797371077, -96.114705818119201155))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_L_09 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.16512246192473984625, -96.1147618866056826925))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_L_10 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.1651506720521086155, -96.11481795509216423))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        //Coloreado de los Salones L

        GroundOverlay salonColorL_05A = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(27.5f)
                .position(new LatLng(19.1649884638197381925, -96.114495561294895385), 3.5f, 12.5f));
        GroundOverlay salonColorL_05B = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(27.5f)
                .position(new LatLng(19.165002568883422577, -96.114523595538136155), 3.5f, 12.5f));
        GroundOverlay salonColorL_06 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(27.5f)
                .position(new LatLng(19.16502372647894915375, -96.1145656469029973075), 7f, 12.5f));
        GroundOverlay salonColorL_07 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(27.5f)
                .position(new LatLng(19.165051936606317923, -96.114621715389478845), 7f, 12.5f));
        GroundOverlay salonColorL_08 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(27.5f)
                .position(new LatLng(19.165094251797371077, -96.114705818119201155), 7f, 12.5f));
        GroundOverlay salonColorL_09 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(27.5f)
                .position(new LatLng(19.16512246192473984625, -96.1147618866056826925), 7f, 12.5f));
        GroundOverlay salonColorL_10 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(27.5f)
                .position(new LatLng(19.1651506720521086155, -96.11481795509216423), 7f, 12.5f));
        GroundOverlay escaleraL_10 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.up))
                .bearing(29f)
                .position(new LatLng(19.165071148380147, -96.1146650267993), 4f, 8f));

        //Coloreado de los Salones I
        GroundOverlay escaleraI_11 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.up))
                .bearing(29f)
                .position(new LatLng(19.165152003003467, -96.11441556126738), 4f, 8f));
        GroundOverlay salonColorI_11 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.1651766473747486815, -96.114465796186761815), 8f, 12f));
        GroundOverlay salonColorI_12 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.1652104684658128635, -96.114533146899503635), 8f, 12f));
        GroundOverlay salonColorI_13 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.1652442895568770455, -96.114600497612245455), 8f, 12f));
        GroundOverlay salonColorI_14 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.16527811064794122725, -96.1146678483249872725), 8f, 12f));
        GroundOverlay salonColorI_15 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.165311931739005409, -96.11473519903772909), 8f, 12f));
        ArrayList<GroundOverlay> TempSalonColor = new ArrayList<>();
        ArrayList<Marker> TempSalones = new ArrayList<>();

        //Introducir tag a los salones L para poder cambiarles de color
        salonColorL_05A.setTag("L_05A");
        salonColorL_05B.setTag("L_05B");
        salonColorL_06.setTag("L_06");
        salonColorL_07.setTag("L_07");
        salonColorL_08.setTag("L_08");
        salonColorL_09.setTag("L_09");
        salonColorL_10.setTag("L_10");

        //Introducir tag a los salones I para poder cambiarles de color
        salonColorI_11.setTag("I_11");
        salonColorI_12.setTag("I_12");
        salonColorI_13.setTag("I_13");
        salonColorI_14.setTag("I_14");
        salonColorI_15.setTag("I_15");

        TempSalonColor.add(salonColorL_05A);
        TempSalonColor.add(salonColorL_05B);
        TempSalonColor.add(salonColorL_06);
        TempSalonColor.add(salonColorL_07);
        TempSalonColor.add(salonColorL_08);
        TempSalonColor.add(salonColorL_09);
        TempSalonColor.add(salonColorL_10);

        TempSalonColor.add(salonColorI_11);
        TempSalonColor.add(salonColorI_12);
        TempSalonColor.add(salonColorI_13);
        TempSalonColor.add(salonColorI_14);
        TempSalonColor.add(salonColorI_15);

        //Agregando los tags de los salones L
        Salon_L_05A.setTag("L_05A");
        Salon_L_05B.setTag("L_05B");
        Salon_L_06.setTag("L_06");
        Salon_L_07.setTag("L_07");
        Salon_L_08.setTag("L_08");
        Salon_L_09.setTag("L_09");
        Salon_L_10.setTag("L_10");

        //Agregando los tags de los salones I
        Salon_I_11.setTag("I_11");
        Salon_I_12.setTag("I_12");
        Salon_I_13.setTag("I_13");
        Salon_I_14.setTag("I_14");
        Salon_I_15.setTag("I_15");

        //Agregando los salones 0 al array de salones
        TempSalones.add(Salon_L_05A);
        TempSalones.add(Salon_L_05B);
        TempSalones.add(Salon_L_06);
        TempSalones.add(Salon_L_07);
        TempSalones.add(Salon_L_08);
        TempSalones.add(Salon_L_09);
        TempSalones.add(Salon_L_10);

        //Agregando los salones I al array de salones
        TempSalones.add(Salon_I_11);
        TempSalones.add(Salon_I_12);
        TempSalones.add(Salon_I_13);
        TempSalones.add(Salon_I_14);
        TempSalones.add(Salon_I_15);

        RefreshHorarios(TempSalones, TempSalonColor);
    }

    void AddFloor1(){
        mMap.clear();
        //Marcadores de los salones O
        Marker Salon_O_01 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.16486648147674, -96.11457766258434))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_O_02 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.16489977081632, -96.11464484860815))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_O_03 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.164927639915334, -96.11470184555166))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_O_04 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.164958042563434, -96.1147702418839))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        //Dibujo del edificio O
        Polygon PolygonSalonO01 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.164895858174816, -96.11452816234457),
                        new LatLng(19.164925785779197, -96.1145886796875425),
                        new LatLng(19.16485532128611, -96.1146307568429025),
                        new LatLng(19.16482555201609, -96.11457107769027))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonO02 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.164925785779197, -96.1145886796875425),
                        new LatLng(19.164955713383578,-96.114649197030515 ),
                        new LatLng(19.16488509055613, -96.114690435995535),
                        new LatLng(19.16485532128611, -96.1146307568429025))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonO03 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.164955713383578,-96.114649197030515),
                        new LatLng(19.164985640987959, -96.1147097143734875),
                        new LatLng(19.16491485982615, -96.1147501151481675),
                        new LatLng(19.16488509055613, -96.114690435995535))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonO04 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.164985640987959, -96.1147097143734875),
                        new LatLng(19.16501556859234, -96.11477023171646),
                        new LatLng(19.16494462909617, -96.1148097943008),
                        new LatLng(19.16491485982615, -96.1147501151481675))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));

        //Marcadores de los salones I
        Marker Salon_I_03B = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.16532038701177145475,-96.1147520367159145475))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_I_03A = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.16530347646623936375,-96.1147183613595436375))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_I_02B = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.16528656592070727275,-96.1146846860031727275))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_I_02A = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.165269655375175182,-96.11465101064680182))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_I_01B = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.165252744829643091,-96.11461733529043091))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_I_01A = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.165235834284111,-96.11458365993406))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        //Dibujo del edificio I
        Polygon PolygonSalonIEscaleras = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165189166759674,-96.11437111633568),
                        new LatLng(19.165206211872836545,-96.11440483778739636),
                        new LatLng(19.165113261785596636,-96.11445940387338545),
                        new LatLng(19.165096485807695, -96.11442577461236))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonICub2 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165206211872836545,-96.11440483778739636),
                        new LatLng(19.165223256985999091,-96.11443855923911273),
                        new LatLng(19.165130037763498273	,-96.11449303313441091),
                        new LatLng(19.165113261785596636,-96.11445940387338545))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonICub1 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165223256985999091,-96.11443855923911273),
                        new LatLng(19.165240302099161636,-96.11447228069082909),
                        new LatLng(19.165146813741399909,-96.11452666239543636),
                        new LatLng(19.165130037763498273	,-96.11449303313441091))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonILab = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165240302099161636,-96.11447228069082909),
                        new LatLng(19.165274392325486727, -96.11453972359426182),
                        new LatLng(19.165180365697203182	,-96.11459392091748727),
                        new LatLng(19.165146813741399909,-96.11452666239543636))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_01A = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165274392325486727, -96.11453972359426182),
                        new LatLng(19.165291437438649273,-96.11457344504597818),
                        new LatLng(19.165197141675104818	,-96.11462755017851273),
                        new LatLng(19.165180365697203182	,-96.11459392091748727))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_01B = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165291437438649273,-96.11457344504597818),
                        new LatLng(19.165308482551811818,-96.11460716649769455),
                        new LatLng(19.165213917653006455	,-96.11466117943953818),
                        new LatLng(19.165197141675104818	,-96.11462755017851273))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_02A = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165308482551811818,-96.11460716649769455),
                        new LatLng(19.165325527664974364, -96.11464088794941091),
                        new LatLng(19.165230693630908091	,-96.11469480870056364),
                        new LatLng(19.165213917653006455,-96.11466117943953818))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_02B = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165325527664974364, -96.11464088794941091),
                        new LatLng(19.165342572778136909,-96.11467460940112727),
                        new LatLng(19.165247469608809727,-96.11472843796158909),
                        new LatLng(19.165230693630908091,-96.11469480870056364))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_03A = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165342572778136909,-96.11467460940112727),
                        new LatLng(19.165359617891299455,-96.11470833085284364),
                        new LatLng(19.165264245586711364,-96.11476206722261455),
                        new LatLng(19.165247469608809727,-96.11472843796158909))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonI_03B = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165359617891299455,-96.11470833085284364),
                        new LatLng(19.165376663004462, -96.11474205230456),
                        new LatLng(19.165281021564613, -96.11479569648364),
                        new LatLng(19.165264245586711364,-96.11476206722261455))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));

        //Marcadores de los salones L
        Marker Salon_L_04 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.1651506720521086155, -96.11481795509216423))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        //Dibujo del edificio L
        Polygon PolygonLabort2Quimica = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165031765661, -96.11445371625378),
                        new LatLng(19.165073861589678923, -96.11453774161215),
                        new LatLng(19.164973591368219385, -96.11459355219384462),
                        new LatLng(19.164931056914792, -96.11450937209277)
                )
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonTallerTPF = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165073861589678923, -96.11453774161215),
                        new LatLng(19.165101925542131538, -96.11459375851773),
                        new LatLng(19.165001947670504308, -96.11464967226122769),
                        new LatLng(19.164973591368219385, -96.11459355219384462)
                )
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonTallerMM = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165101925542131538, -96.11459375851773),             //NE
                        new LatLng(19.165115957518357846, -96.11462176697052),          //NO
                        new LatLng(19.165016125821646769, -96.11467773229491923),       //SO
                        new LatLng(19.165001947670504308, -96.11464967226122769)        //SE
                )
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonLEscaleras = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165115957518357846, -96.11462176697052),
                        new LatLng(19.165129989494584154, -96.11464977542331),
                        new LatLng(19.165030303972789231, -96.11470579232861077),
                        new LatLng(19.165016125821646769, -96.11467773229491923)
                )
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonOficFaculCQ = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165129989494584154, -96.11464977542331),
                        new LatLng(19.165158053447036769, -96.11470579232889),
                        new LatLng(19.165058660275074154, -96.11476191239599385),
                        new LatLng(19.165030303972789231, -96.11470579232861077)
                )
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonCubProf = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165158053447036769, -96.11470579232889),
                        new LatLng(19.165186117399489385, -96.11476180923447),
                        new LatLng(19.165087016577359077, -96.11481803246337692),
                        new LatLng(19.165058660275074154, -96.11476191239599385)
                )
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonL_04 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.165186117399489385, -96.11476180923447),
                        new LatLng(19.165214181351942, -96.11481782614005),
                        new LatLng(19.165115372879644, -96.11487415253076),
                        new LatLng(19.165087016577359077, -96.11481803246337692)
                )
                .strokeColor(Color.BLACK)
                .strokeWidth(5));

        //Marcadores de los salones P
        Marker Salon_P_01 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.164631101179213875, -96.114566931995753125 ))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_P_02 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.164661108008027625, -96.114625856775179375))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_P_03 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.164691114836841375, -96.114684781554605625))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        Marker Salon_P_04 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.164721121665655125, -96.114743706334031875))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        //Dibujo del edificio P
        Polygon PolygonSalonP01 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.164651250896338, -96.11451835886677),
                        new LatLng(19.1646813368924145, -96.1145770321891),
                        new LatLng(19.164610872294827, -96.1146157565818325),
                        new LatLng(19.164580944633276, -96.11455658034531))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));
        Polygon PolygonSalonP02 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.1646813368924145, -96.1145770321891),
                        new LatLng(19.164711422888491, -96.11463570551143),
                        new LatLng(19.164640799956378, -96.114674932818355),
                        new LatLng(19.164610872294827, -96.1146157565818325))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));

        Polygon PolygonSalonP03 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.164711422888491, -96.11463570551143),
                        new LatLng(19.1647415088845675, -96.11469437883376),
                        new LatLng(19.164670727617929, -96.1147341090548775),
                        new LatLng(19.164640799956378, -96.114674932818355))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));

        Polygon PolygonSalonP04 = mMap.addPolygon(new PolygonOptions()
                .add(new LatLng(19.1647415088845675, -96.11469437883376),
                        new LatLng(19.164771594880644, -96.11475305215609),
                        new LatLng(19.16470065527948, -96.1147932852914),
                        new LatLng(19.164670727617929, -96.1147341090548775))
                .strokeColor(Color.BLACK)
                .strokeWidth(5));

        //Coloreado de los Salones P
        GroundOverlay salonColorP_01 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29.5f)
                .position(new LatLng(19.164631101179213875, -96.114566931995753125 ), 7f, 9f));
        GroundOverlay salonColorP_02 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29.5f)
                .position(new LatLng(19.164661108008027625, -96.114625856775179375), 7f, 9f));
        GroundOverlay salonColorP_03 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29.5f)
                .position(new LatLng(19.164691114836841375, -96.114684781554605625), 7f, 9f));
        GroundOverlay salonColorP_04 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29.5f)
                .position(new LatLng(19.164721121665655125, -96.114743706334031875), 7f, 9f));

        //Coloreado de los Salones O
        GroundOverlay salonColorO_01 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29.5f)
                .position(new LatLng(19.16487562931405325,-96.11457966914132125), 7f, 9f));
        GroundOverlay salonColorO_02 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29.5f)
                .position(new LatLng(19.16490547775125375,-96.11463976738912375), 7f, 9f));
        GroundOverlay salonColorO_03 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.16493532618845425,-96.11469986563692625), 7f, 9f));
        GroundOverlay salonColorO_04 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(28f)
                .position(new LatLng(19.16496517462565475,-96.11475996388472875), 7f, 9f));
        //Coloreado de los salones L
        GroundOverlay escaleraL_00 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.up))
                .bearing(29f)
                .position(new LatLng(19.165071148380147, -96.1146650267993), 4f, 8f));
        GroundOverlay salonColorL_04 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(28f)
                .position(new LatLng(19.1651506720521086155, -96.11481795509216423), 6.7f, 12.5f));

        //Coloreado de los Salones I
        GroundOverlay salonColorI_03B = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.16532038701177145475,-96.1147520367159145475), 4f, 12f));
        GroundOverlay salonColorI_03A = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.16530347646623936375,-96.1147183613595436375), 4f, 12f));
        GroundOverlay salonColorI_02B = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.16528656592070727275,-96.1146846860031727275), 4f, 12f));
        GroundOverlay salonColorI_02A = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.165269655375175182,-96.11465101064680182), 4f, 12f));
        GroundOverlay salonColorI_01B = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.165252744829643091,-96.11461733529043091), 4f, 12f));
        GroundOverlay salonColorI_01A = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.close))
                .bearing(29f)
                .position(new LatLng(19.165235834284111,-96.11458365993406), 4f, 12f));


        GroundOverlay escaleraI_00 = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.up))
                .bearing(29f)
                .position(new LatLng(19.165152003003467, -96.11441556126738), 4f, 8f));


        ArrayList<GroundOverlay> TempSalonColor = new ArrayList<>();
        ArrayList<Marker> TempSalones = new ArrayList<>();

        //Introducir tag a los salones O para poder cambiarles de color
        salonColorO_01.setTag("O_01");
        salonColorO_02.setTag("O_02");
        salonColorO_03.setTag("O_03");
        salonColorO_04.setTag("O_04");

        //Introducir tag a los salones P para poder cambiarles de color
        salonColorP_01.setTag("P_01");
        salonColorP_02.setTag("P_02");
        salonColorP_03.setTag("P_03");
        salonColorP_04.setTag("P_04");

        //Introducir tag a los salones I para poder cambiarles de color
        salonColorI_03B.setTag("I_03B");
        salonColorI_03A.setTag("I_03A");
        salonColorI_02B.setTag("I_02B");
        salonColorI_02A.setTag("I_02A");
        salonColorI_01B.setTag("I_01B");
        salonColorI_01A.setTag("I_01A");

        //Introducir tag a los salones L para poder cambiarles de color
        salonColorL_04.setTag("L_04");

        TempSalonColor.add(salonColorO_01);
        TempSalonColor.add(salonColorO_02);
        TempSalonColor.add(salonColorO_03);
        TempSalonColor.add(salonColorO_04);

        TempSalonColor.add(salonColorP_01);
        TempSalonColor.add(salonColorP_02);
        TempSalonColor.add(salonColorP_03);
        TempSalonColor.add(salonColorP_04);

        TempSalonColor.add(salonColorI_03B);
        TempSalonColor.add(salonColorI_03A);
        TempSalonColor.add(salonColorI_02B);
        TempSalonColor.add(salonColorI_02A);
        TempSalonColor.add(salonColorI_01B);
        TempSalonColor.add(salonColorI_01A);

        TempSalonColor.add(salonColorL_04);

        //Agregando los tags de los salones O
        Salon_O_01.setTag("O_01");
        Salon_O_02.setTag("O_02");
        Salon_O_03.setTag("O_03");
        Salon_O_04.setTag("O_04");

        //Agregando los tags de los salones P
        Salon_P_01.setTag("P_01");
        Salon_P_02.setTag("P_02");
        Salon_P_03.setTag("P_03");
        Salon_P_04.setTag("P_04");

        //Agregando los tags de los salones I
        Salon_I_03B.setTag("I_03B");
        Salon_I_03A.setTag("I_03A");
        Salon_I_02B.setTag("I_02B");
        Salon_I_02A.setTag("I_02A");
        Salon_I_01B.setTag("I_01B");
        Salon_I_01A.setTag("I_01A");

        //Agregando los tags de los salones L
        Salon_L_04.setTag("L_04");

        //Agregando los salones 0 al array de salones
        TempSalones.add(Salon_O_01);
        TempSalones.add(Salon_O_02);
        TempSalones.add(Salon_O_03);
        TempSalones.add(Salon_O_04);

        //Agregando los salones P al array de salones
        TempSalones.add(Salon_P_01);
        TempSalones.add(Salon_P_02);
        TempSalones.add(Salon_P_03);
        TempSalones.add(Salon_P_04);

        //Agregando los salones I al array de salones
        TempSalones.add(Salon_I_03B);
        TempSalones.add(Salon_I_03A);
        TempSalones.add(Salon_I_02B);
        TempSalones.add(Salon_I_02A);
        TempSalones.add(Salon_I_01B);
        TempSalones.add(Salon_I_01A);

        TempSalones.add(Salon_L_04);

        RefreshHorarios(TempSalones, TempSalonColor);
    }

    private ClusterManager<MyItem> clusterManager;
    private ClusterManager Prueba;
    private void setUpClusterer() {
        // Position the map.
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.164345108507927, -96.11503944143145), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<MyItem>(this, mMap);
        Prueba = new ClusterManager(this, mMap);


        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);


        // Add cluster items (markers) to the cluster manager.

    }

    private void addItems(ArrayList<Marker> Salones) {
        for(int i = 0; i < Salones.size();i++){
            LatLng salonOpos = Salones.get(i).getPosition();
            String salonOtitle = Salones.get(i).getTitle();
            String salonOsnip = Salones.get(i).getSnippet();
            MyItem salonCluster = new MyItem(salonOpos.latitude, salonOpos.longitude, salonOtitle, salonOsnip);
            clusterManager.addItem(salonCluster);
        }
        /*
        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 10000d;
            lat1 = lat1 + offset;
            lng1 = lng1 + offset;
            MyItem offsetItem = new MyItem(lat1, lng1, "Title " + i, "Snippet " + i);
            clusterManager.addItem(offsetItem);
        }*/
    }

    //Eventos de la ventana de información
    /*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add markers to the map and do other map setup.
        //Crea un marcador
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(19.165483789931418, -96.11413851574602))
                .title("Facultad De Ingenieria"));
        // Set a listener for info window events.
        googleMap.setOnInfoWindowClickListener(this);
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();
    }
     */
}
