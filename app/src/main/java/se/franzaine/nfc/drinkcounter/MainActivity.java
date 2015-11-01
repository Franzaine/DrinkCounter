package se.franzaine.nfc.drinkcounter;

import android.content.DialogInterface;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {

    public static final int NUM_FREE_DRINKS = 4;
    NfcAdapter nfcAdapter;
    final static private String TAG = "MainActivity";
    final private static char[] hexArray = "0123456789ABCDEF".toCharArray();

    RecyclerView partyPeopleRecyclerView;
    Button addPartyPersonButton;

    PartyPeopleAdapter partyPeopleAdapter;
    List<String> partyPeopleList = new ArrayList<>();
    Map<String, PartyPerson> partyPeopleMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addPartyPersonButton = (Button) findViewById(R.id.add_party_person_button);
        partyPeopleRecyclerView = (RecyclerView) findViewById(R.id.party_people_recycler_view);

        partyPeopleRecyclerView.setHasFixedSize(true);
        partyPeopleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        partyPeopleAdapter = new PartyPeopleAdapter();
        partyPeopleRecyclerView.setAdapter(partyPeopleAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, Bundle.EMPTY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableReaderMode(this);
        super.onPause();
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        final String tagId = encodeHex(tag.getId());
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                tagIdDiscovered(tagId);
            }
        });
    }

    private void tagIdDiscovered(String tagId) {
        Log.v(TAG, "Tag discovered! ID: " + tagId);
        if (addPartyPersonButton.isPressed()){
            Log.v(TAG, "Add a person request");
            addPartyPerson(tagId);
        } else {
            Log.v(TAG, "Countdown drinks request");
            handleDrinksCounting(tagId);
        }
    }

    private void handleDrinksCounting(String tagId) {
        if (checkIfInList(tagId, partyPeopleList)){
            countDownDrinks(tagId);
        } else {
            Log.v(TAG, "Request denied. Not a guest!");
            shout("Not a guest!");
        }
    }

    private void countDownDrinks(String tagId) {
        PartyPerson partyPerson = partyPeopleMap.get(tagId);
        int drinksLeft = partyPerson.getDrinksLeft();
        String name = partyPerson.getName();
        if (drinksLeft > 0) {
            drinksLeft--;
            partyPeopleMap.put(tagId, new PartyPerson(name, drinksLeft));
            partyPeopleAdapter.setNewList(partyPeopleList, partyPeopleMap);
            Log.v(TAG, "Request accepted. Counted down to: " + drinksLeft);
            shout("Enjoy your drink! Drinks left: " + drinksLeft);
        } else {
            shout("No free drinks left, sorry!");
        }
    }

    private void addPartyPerson(String tagId) {
        if(checkIfInList(tagId, partyPeopleList)){
            Log.v(TAG, "Request denied. Already a guest!");
            shout("Already a guest!");
        } else {
            Log.v(TAG, "Request accepted. Name?");
            getPartyPersonName(tagId);
        }
    }

    private void getPartyPersonName(final String tagId) {
        View view = getLayoutInflater().inflate(R.layout.alert_edit_text, null);
        final EditText editText = (EditText) view.findViewById(R.id.alert_edit_text);
        new AlertDialog.Builder(this)
                .setTitle("Name?")
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shout("Adding of PP aborted!");
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        partyPeopleList.add(tagId);
                        partyPeopleMap.put(tagId, new PartyPerson(editText.getText().toString(), NUM_FREE_DRINKS));
                        partyPeopleAdapter.setNewList(partyPeopleList, partyPeopleMap);
                    }
                })
                .create()
                .show();
    }

    private static boolean checkIfInList(String tagId, List<String> partyPeopleList) {
        return partyPeopleList.contains(tagId);
    }

    private void shout(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Encodes a byte array into a hexadecimal string having two characters per byte
     * @param bytes the input byte[]
     * @return the resulting hex string
     */
    public static String encodeHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int i = 0; i < bytes.length; i++ ) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
