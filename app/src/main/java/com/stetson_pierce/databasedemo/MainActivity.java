package com.stetson_pierce.databasedemo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    /*  ---------------------------
    This App will demo a few easy to use SQL concepts so that you
    can begin using databases in your apps. This is NOT in any way
    a comprehensive tutorial or explanation. It simply introduces
    a few common use cases for a database and how you would accomplish
    those tasks. I highly suggest watching Bill Weinman's course on
    SQLite here:
    http://www.lynda.com/SQLite-3-with-PHP-tutorials/essential-training/66386-2.html

    You can also reference the Android developer site here:
    http://developer.android.com/training/basics/data-storage/databases.html
        --------------------------- */

    public final String LOG_TITLE = "SQLite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Custom Code

        Log.i(LOG_TITLE, "----------------BEGIN----------------");

        try {
            // Create a Database called "Users" if it doesn't exist,
            // otherwise it just opens it and stores a reference in
            // the myDatabase (SQLiteDatabase) variable.
            SQLiteDatabase myDatabase = this.openOrCreateDatabase("Users", MODE_PRIVATE, null);

            // Create a table called "users" in the "Users" Database
            // only if it doesn't already exist.
            myDatabase.execSQL(
                    "CREATE TABLE IF NOT EXISTS users (" +
                        "name VARCHAR, " +
                        "age INTEGER(3)," +
                        "id INTEGER PRIMARY KEY" +
                    ")"
            );

            // Add a user to the "users" table of the "Users" database
            // with the value of "Stetson" in the "name" column
            // and "23" in the "age" column.
            myDatabase.execSQL(
                    "INSERT INTO users (name, age) VALUES ('Stetson', 23)"
            );

            // Let's INSERT a few more test users into our "users" table.
            // Notice that we're not adding any value for the id column.
            // This is because it's our primary key, and SQLite will
            // take care of making sure that this is a unique integer value
            // for every row in our table.
            myDatabase.execSQL(
                    "INSERT INTO users (name, age) VALUES ('Rob', 34)"
            );
            myDatabase.execSQL(
                    "INSERT INTO users (name, age) VALUES ('Jon', 40)"
            );
            myDatabase.execSQL(
                    "INSERT INTO users (name, age) VALUES ('Art', 55)"
            );

            // Create a Cursor object to allow us to loop through the
            // results of a query and do something with the results.
            // The Cursor "c" is initialized to the results of the Database
            // method "rawQuery("SOME SQL HERE");
            // -----------------------------------------------------
            // In the SQL statement we're selecting * (everything)
            // from the "users" table, within the "Users" database
            Cursor c = myDatabase.rawQuery(
                    "SELECT * FROM users", null);

            // Now we must get the column indices so that we can get the values
            // from those columns. Normally with databases we don't need to do this,
            // this is unique to SQLite on Android.
            int nameIndex = c.getColumnIndex("name");
            int ageIndex = c.getColumnIndex("age");
            int pKeyIndex = c.getColumnIndex("id");

            // Now we take our Cursor object "c" and move (point) it to the
            // first result of our query "SELECT * FROM users".
            // In this case it should be the entry for the user "Stetson"
            c.moveToFirst();

            // Now we loop through each result as long as c is not null,
            // meaning that there are no more queries to loop through.
            while(c != null) {

                // In here we'll grab the relevant data from our Cursor "c"
                // We use our indices to specify which part of the result
                // we're trying to get.
                // -----------------------------------------------------
                // The first time through this should Log "Stetson:23:1"
                // Which reflects the columns name:age:id
                Log.i(LOG_TITLE, c.getString(nameIndex) + ":" +
                        Integer.toString(c.getInt(ageIndex)) + ":" +
                        Integer.toString(c.getInt(pKeyIndex)));

                // Finally we'll move our Cursor object "c" to the next
                // result from our query "SELECT * FROM users" as long as
                // there is another query to point to.
                // Meaning we're using the .isLast() method to check if we've
                // reached the end our results, and if so we're breaking out of
                // our loop.
                if(!c.isLast())
                    c.moveToNext();
                else
                    break;
            }

            // Now we'll run another query against our "users" table and
            // have it only return users WHERE their age is greater than 30.
            // This should return the users "Rob", "Jon" and "Art".
            // To specify a condition in SQL we use what's called a WHERE clause
            // This will specify what condition needs to be met in order to
            // return a row as part of our query.
            // -----------------------------------------------------
            // For this we'll just reuse our Cursor object "c".
            c = myDatabase.rawQuery(
                    "SELECT * FROM users WHERE age > 30", null);

            // Then we'll move the cursor "c" to the first result of the query.
            c.moveToFirst();

            // We'll add a message to the Log that will clearly show us
            // the two separate queries we've run at this point.
            Log.i(LOG_TITLE, "-------------------------------------");

            // Once again we'll loop through our results and Log the data we care about.
            while(c != null) {
                // The first time through we expect to receive 1 of the tree
                // users mentioned above. Keep in mind that SQL does not guarantee
                // the order of the query results unless you explicitly tell it to.
                Log.i(LOG_TITLE, c.getString(nameIndex) + ":" +
                        Integer.toString(c.getInt(ageIndex)) + ":" +
                        Integer.toString(c.getInt(pKeyIndex)));

                if (!c.isLast())
                    c.moveToNext();
                else
                    break;
            }

            // Next we're going to UPDATE one of the rows in our table with a new age value
            // In this case we're going to UPDATE any users in the "users" table
            // where the "name" value = 'Stetson'
            // This is not the ideal way to go about this as we may have more than 1 'Stetson'
            // in our table and this will UPDATE them all.
            // So to ensure that we get the correct user we need something unique that
            // identifies that user. This is where our primary key comes in handy.
            // -----------------------------------------------------
            // Let's UPDATE the 'Stetson' user with the name value just to see how it's done.
            myDatabase.execSQL(
                    "UPDATE users SET age = 25 WHERE name = 'Stetson'"
            );

            // At this point if we ran a query and got our 'Stetson' user it would
            // look like the following Stetson:25:1
            // -----------------------------------------------------
            // Next we're going to UPDATE that user again. Only this time we'll ensure that
            // we're updating only the 1 user we actually care about. We know that the 'Stetson'
            // user has an id of 1, because it was the first entry into our "users" table
            myDatabase.execSQL(
                    "UPDATE users SET age = 24 WHERE id = 1"
            );

            // At this point if we ran a query and got our 'Stetson' user it should
            // now look like the following Stetson:24:1
            // -----------------------------------------------------
            // What the hell, Jon?! Man Jon is being a real dick!
            // Let's go ahead and DELETE him from our "users" table
            // so we can cut him out of our lives!
            myDatabase.execSQL(
                    "DELETE FROM users WHERE name = 'Jon'"
            );

            // We'll add another break to our Log
            Log.i(LOG_TITLE, "-------------------------------------");

            // Let's make sure that we don't have any more reference to that pesky
            // user 'Jon' in our "users" table.
            // -----------------------------------------------------
            // To do this we'll run a query to grab all the rows from our "users" table.
            // As before we're going to use our Cursor "c" to do this.
            c = myDatabase.rawQuery(
                    "SELECT * FROM users", null);

            // We'll move our Cursor "c" to the first result in our query
            c.moveToFirst();

            // Now we'll loop through and verify we've removed the user 'Jon'
            while(c != null) {
                Log.i(LOG_TITLE, c.getString(nameIndex) + ":" +
                        Integer.toString(c.getInt(ageIndex)) + ":" +
                        Integer.toString(c.getInt(pKeyIndex)));

                if (!c.isLast())
                    c.moveToNext();
                else
                    break;
            }

            // We'll add another break to our Log
            Log.i(LOG_TITLE, "-------------------------------------");

            // Great, that sneaky bastard Jon has been removed from existence
            // That means we only have 3 users in our "users" table.
            // But how would I know that if I didn't explicitly add users?
            // Well, I can use something called an aggregate function to get the
            // total Count of my query results.
            // In this case, let's see how many users I have in the "users" table
            // -----------------------------------------------------
            // We'll first execute another query, only this time we're using the
            // aggregate function count() to get the total number of users.
            c = myDatabase.rawQuery(
                    "SELECT count(*) FROM users", null);

            // We'll move our Cursor "c" to the first, and only, result of our query
            c.moveToFirst();

            // Since aggregate functions only return a single value we don't need to
            // loop through the results, as we know there's only 1 result.
            // So we'll user the .getInt() method to get the Integer value at position 0.
            // This will return the int representation of our total user count.
            // -----------------------------------------------------
            // We'll then log the result so we can see it.
            Log.i(LOG_TITLE, "Total user count = " + Integer.toString(c.getInt(0)));

            // Last thing we'll do is delete our "Users" database for testing purposes.
            // This will ensure that we're creating a new database every time we run the app.
            // Which means we can anticipate the results each time.
            getApplicationContext().deleteDatabase("Users");

            Log.i(LOG_TITLE, "-----------------END-----------------");

            /*  -----------------------------
            Other simple keyword to use

            - AND -
            SELECT * FROM users WHERE name = 'Stetson' AND age = 24

            - OR -
            SELECT * FROM users WHERE name = 'Art' OR name = 'Jon'

            - LIKE -
            SELECT * FROM users WHERE name LIKE 's%'
            SELECT * FROM users WHERE name LIKE '%o%'
            SELECT * FROM users WHERE name LIKE '%n'

            - LIMIT -
            SELECT * FROM users LIMIT 1
                -----------------------------   */
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
