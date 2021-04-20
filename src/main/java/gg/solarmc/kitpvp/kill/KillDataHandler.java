package gg.solarmc.kitpvp.kill;

import gg.solarmc.kitpvp.KitpvpConfig;
import gg.solarmc.kitpvp.kill.result.KillResult;
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


    CentralisedFuture<KillResult> handleKill(Player killer, Player killed, Set<Player> assisters) {
        return center.transact(transaction -> {
            killer.getSolarPlayer().getData(KitPvpKey.INSTANCE).addKills(transaction,1);
            killer.getSolarPlayer().getData(KitPvpKey.INSTANCE).addKillstreaks(transaction,1);
            killed.getSolarPlayer().getData(KitPvpKey.INSTANCE).addDeaths(transaction,1);
            killed.getSolarPlayer().getData(KitPvpKey.INSTANCE).resetCurrentKillstreaks(transaction);
            killed.getSolarPlayer().getData(KitPvpKey.INSTANCE).addExperience(transaction,1);

            for (UUID assisterID : assisters) {
                try { //blocking ""safely""
                    SolarPlayer ass = center.lookupPlayer(assisterID).get().orElseThrow(() -> new IllegalStateException("It is not present!"));

                    ass.getData(KitPvpKey.INSTANCE).addAssists(transact,1);
                } catch (InterruptedException | ExecutionException e) {
                    throw new UncheckedAwaitExeception(e); //handle
                }
            }

        }));

            killer.getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction, BigDecimal.valueOf(config.killMoney()));

            return new KillResult(false);
        });
    }

    CentralisedFuture<?> handleDeath(Player killed, Set<Player> assisters) {
        return center.runTransact(transaction -> {
            killed.getSolarPlayer().getData(KitPvpKey.INSTANCE).addDeaths(transaction,1);
            killed.getSolarPlayer().getData(KitPvpKey.INSTANCE).resetCurrentKillstreaks(transaction);


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
