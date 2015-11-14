package com.stetson_pierce.databasedemo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.stetson_pierce.databasedemo.R;

public class MainActivity extends AppCompatActivity {

    /*  ---------------------------
    This App will demo a few easy to use SQL concepts so that you
    can begin using databases in your apps. This is NOT in any way
    a comprehensive tutorial or explanation. It simply introduces
    a few common use cases for a database and how you would accomplish
    those tasks. I highly suggest watching Bill Weinman's course on
    SQLite here:
    http://www.lynda.com/SQLite-3-with-PHP-tutorials/essential-training/66386-2.html

    You can also reference the Android devloper site here:
    http://developer.android.com/training/basics/data-storage/databases.html
        --------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Custom Code

        try {
            // Create a Database called "Users" if it doesn't exist,
            // otherwise it just opens it and stores a reference in
            // the myDatebase (SQLiteDatebase) variable
            //SQLiteDatabase myDatebase = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);
            SQLiteDatabase myDatabase = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);

            // Create a table called "users" in the "Users" Datebase
            // only if it doesn't exist already exist
            myDatabase.execSQL(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "name VARCHAR, " +
                            "age INTEGER(3)," +
                            "id INTEGER PRIMARY KEY" +
                            ")");

            // Add a user to the "users" table of the "Users" database
            // with the value of "Stetson" in the "name" column
            // and "23" in the "age" column
            myDatabase.execSQL(
                    "INSERT INTO users (name, age) VALUES ('Stetson', 23)");

            // A few more test users
            myDatabase.execSQL(
                    "INSERT INTO users (name, age) VALUES ('Rob', 34)");
            myDatabase.execSQL(
                    "INSERT INTO users (name, age) VALUES ('Jon', 40)");
            myDatabase.execSQL(
                    "INSERT INTO users (name, age) VALUES ('Art', 55)");

            // Create a Cursor object to allow us to loop through the
            // results of a query and do something with the results.
            // The Cursor "c" is initialized to the results of the Database
            // method "rawQuery("SOME SQL HERE");
            // -----------------------------------------------------
            // In the SQL statement we're selecting * (everything)
            // from the "users" table, within the "Users" database
            Cursor c = myDatabase.rawQuery(
                    "SELECT * FROM users", null);

            // Now we must get the column indices so that we can get
            // the values from them (this is unique to SQLite on Android
            // Normally with databases we don't need to do this
            int nameIndex = c.getColumnIndex("name");
            int ageIndex = c.getColumnIndex("age");
            int pKeyIndex = c.getColumnIndex("id");

            // Now we take our Cursor object "c" and move it to the
            // first result of our query "SELECT * FROM users"
            // In this case it should be the entry for the user "Stetson"
            c.moveToFirst();

            // Now we loop through each result as long as c is not null,
            // meaning that there are no more queries to loop through
            while(c != null) {

                // In here we'll grab the relevant data from our Cursor "c"
                // We use our indices to specify which part of the result
                // we're trying to get
                // -----------------------------------------------------
                // The first time through this should Log "Stetson:23:1"
                Log.i("SQLite:Users:users:name", c.getString(nameIndex) + ":" +
                        Integer.toString(c.getInt(ageIndex)) + ":" +
                        Integer.toString(c.getInt(pKeyIndex)));

                // Finally we'll move our Cursor object "c" to the next
                // result from our query "SELECT * FROM users"
                if(!c.isLast())
                    c.moveToNext();
                else
                    c = null;
            }

            // The next query we'll run against our "users" table
            // withing the "Users" database is to only return users
            // WHERE their age is greater than 30. In this case that
            // should return the users "Rob", "Jon" and "Art"
            // For this we'll just reuse our Cursor object "c"
            c = myDatabase.rawQuery(
                    "SELECT * FROM users WHERE age > 30",
                    null);

            // Then we'll move the cursor "c" to the first result
            // of the Query
            c.moveToFirst();

            // We'll add a message to the Log that will clearly
            // show us the two separate queries we've run at this point
            Log.i("SQLite:BREAK", "-------------------------------------");

            // Once again we'll loop through our results and Log
            // the data we care about
            while(c != null) {
                // The first time through we expect to receive 1
                // of the three users mentioned above, however,
                // SQL doesn't guarantee the order of the query results
                // unless you explicitly tell it to
                Log.i("SQLite:Users:users:name", c.getString(nameIndex) + ":" +
                        Integer.toString(c.getInt(ageIndex)) + ":" +
                        Integer.toString(c.getInt(pKeyIndex)));

                if (!c.isLast())
                    c.moveToNext();
                else
                    c = null;
            }

            // Then we're going to UPDATE one of the rows in our table
            myDatabase.execSQL(
                    "UPDATE users SET age = 24 WHERE name = 'Stetson'");

            // What the hell, Jon?! Man Jon is being a real dick!
            // Let's go ahead and remove him from our "users" table
            // so we can cut him out of our lives
            myDatabase.execSQL(
                    "DELETE FROM users WHERE name = 'Jon'");

            /*  -----------------------------
            Other simple keyword to use

            - AND -
            SELECT * FROM users WHERE name = 'Stetson' AND age = 23

            - OR -
            SELECT * FROM users WHERE name = 'Art' OR name = 'Jon'

            - LIKE -
            SELECT * FROM users WHERE name LIKE 's%'
            SELECT * FROM users WHERE name LIKE '%o%'
            SELECT * FROM users WHERE name LIKE '%n'

            - LIMIT -
            SELECT * FROM users LIMIT 1
                -----------------------------   */

            // Last thing we'll do is remove the database for testing
            // This will allow us to expect the same results each time
            // we run the app
            getApplicationContext().deleteDatabase("Users");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
