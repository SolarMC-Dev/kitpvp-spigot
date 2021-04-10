package gg.solarmc.kitpvp.kill;

import gg.solarmc.kitpvp.KitpvpConfig;
import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.SolarPlayer;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class KillDataHandler {

    private final DataCenter center;
    private final KitpvpConfig config;


    public KillDataHandler(DataCenter center, KitpvpConfig config) {
        this.center = center;
        this.config = config;
    }

    CentralisedFuture<?> handleKill(UUID killerId, UUID killedId, Set<UUID> assisters) {

        CentralisedFuture<Optional<SolarPlayer>> killedFuture = center.lookupPlayer(killedId);
        CentralisedFuture<Optional<SolarPlayer>> killerFuture = center.lookupPlayer(killerId);

        CentralisedFuture<Void> combine = (CentralisedFuture<Void>) CentralisedFuture.allOf(killedFuture,killerFuture);
        //can you fix this on centralized future (casting) ? I'm sure as hell fixing this for taskfuture >:)

        //the only reason i'm doing this combine block (and running this asynchronously)
        // is because i want to be able to check if the players are present and fart exceptions
        // Otherwise i could run synchronously on a new thread
        // powered by the transact, like i do with assisters, and just await, since blocking operations are fine
        // inside of a transact.

        return combine.thenApply(ignored -> {

            //optionals in this case being empty signals something went wrong as these players are on the server at some point
            // in order to be logged here and thus
            // have joined before.
            SolarPlayer killed = killedFuture.join().orElseThrow(() -> new IllegalStateException("Killed player was missing"));
            SolarPlayer killer = killerFuture.join().orElseThrow(() -> new IllegalStateException("Killer player was missing"));

            return new CombinedPlayers(killed,killer);
        }).thenCompose((fuck) -> center.runTransact(transact -> {
            fuck.getKiller().getData(KitPvpKey.INSTANCE).addKills(transact,1);
            fuck.getKilled().getData(KitPvpKey.INSTANCE).addDeaths(transact, 1);

            //normal functionality

            //FUN functionality

            for (UUID assisterID : assisters) {
                try { //blocking ""safely""
                    SolarPlayer ass = center.lookupPlayer(assisterID).get().orElseThrow(() -> new IllegalStateException("It is not present!"));

                    ass.getData(KitPvpKey.INSTANCE).addAssists(transact,1);
                } catch (InterruptedException | ExecutionException e) {
                    throw new UncheckedAwaitExeception(e); //handle
                }
            }

        }));

    }

    CentralisedFuture<?> handleDeath(UUID killedId, Set<UUID> assisters) {


        CentralisedFuture<Optional<SolarPlayer>> killedFuture = center.lookupPlayer(killedId);

        return killedFuture
                .thenApply(optional -> optional.orElseThrow(() -> new IllegalStateException("Killed player is missing!")))
                .thenCompose(man -> center.runTransact(transact -> {
                    man.getData(KitPvpKey.INSTANCE).addDeaths(transact,1);

                    for (UUID assisterId : assisters) {
                        //blockig time!
                        try { //blocking ""safely""
                            SolarPlayer ass = center.lookupPlayer(assisterId).get().orElseThrow(() -> new IllegalStateException("It is not present!"));

                            ass.getData(KitPvpKey.INSTANCE).addAssists(transact,1);
                        } catch (InterruptedException | ExecutionException e) {
                            throw new UncheckedAwaitExeception(e); //handle
                        }
                    }
                }));
    }

    public static final class CombinedPlayers {
        public SolarPlayer getKilled() {
            return killed;
        }

        public SolarPlayer getKiller() {
            return killer;
        }

        private final SolarPlayer killed;
        private final SolarPlayer killer;

        public CombinedPlayers(SolarPlayer killed, SolarPlayer killer) {
            this.killed = killed;
            this.killer = killer;
        }
    }

    public static final class UncheckedAwaitExeception extends RuntimeException {
        public UncheckedAwaitExeception(Throwable throwable) {
            super(throwable);
        }
    }



}
