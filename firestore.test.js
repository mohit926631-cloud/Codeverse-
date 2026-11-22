const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_coder_123";
const BOB_UID = "bob_coder_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validProfile(uid, ts) {
  return {
    userId: uid,
    displayName: "Nova Dev",
    avatarPreset: "cyber_bot",
    languageCode: "en",
    dailyGoalMinutes: 15,
    learningTrack: "Full-Stack Web",
    xp: 240,
    level: 2,
    streakDays: 3,
    gems: 150,
    completedLessonIds: ["w1_m1_l1"],
    unlockedAchievementIds: ["first_spark"],
    completedMissionIds: ["m_daily_lesson"],
    defeatedBossWorldIds: [],
    byteColorTheme: "lavender_prime",
    byteAccessory: "neon_visor",
    onboardingDone: true,
    proMember: false,
    createdAt: ts,
    updatedAt: ts,
  };
}

function validProject(uid, ts) {
  return {
    userId: uid,
    title: "Neon Portfolio",
    description: "Responsive dark hero card",
    htmlCode: "<h1>Hello Codeverse</h1>",
    cssCode: "body { background: #191B30; color: #fff; }",
    jsCode: "console.log('ready');",
    templateTag: "HTML/CSS/JS",
    createdAt: ts,
    updatedAt: ts,
  };
}

function validLeaderboard(uid, ts, visible = true) {
  return {
    userId: uid,
    displayName: "Nova Dev",
    avatarPreset: "cyber_bot",
    xp: 240,
    level: 2,
    streakDays: 3,
    league: "Cyber League",
    publicVisible: visible,
    createdAt: ts,
    updatedAt: ts,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users cannot read or write users, projects, or leaderboard", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const ts = new Date(Date.now() - 1000);
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID, ts)));
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("projects").doc("proj_1").get()
  );
  await assertFails(
    unauthDb.collection("leaderboard").where("publicVisible", "==", true).get()
  );
});

test("Owner can create, read, and partially update their own profile; rejects ghost fields & immutable createdAt", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID, created))
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());

  // Partial update allowed fields
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      xp: 340,
      level: 3,
      gems: 175,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject mutating createdAt
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject ghost field
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject invalid languageCode
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      languageCode: "fr",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject future timestamp
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      xp: 500,
      updatedAt: new Date(Date.now() + 60000),
    })
  );
});

test("Cross-user access to profile and projects is denied", async () => {
  const ts = new Date(Date.now() - 5000);
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx.firestore().collection("users").doc(BOB_UID).set(validProfile(BOB_UID, ts));
    await ctx
      .firestore()
      .collection("users")
      .doc(BOB_UID)
      .collection("projects")
      .doc("bob_proj")
      .set(validProject(BOB_UID, ts));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).update({ xp: 9999, updatedAt: new Date(Date.now() - 1000) })
  );
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("projects").get()
  );
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("projects").doc("bob_proj").get()
  );
});

test("Owner can CRUD projects under /users/{uid}/projects and rejects @DocumentId 'id' field", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("projects")
      .doc("proj_1")
      .set(validProject(ALICE_UID, created))
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("projects").get()
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("projects")
      .doc("proj_1")
      .update({
        title: "Updated Portfolio",
        updatedAt: new Date(Date.now() - 1000),
      })
  );

  // Reject extra 'id' field
  await assertFails(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("projects")
      .doc("proj_bad")
      .set({
        id: "proj_bad",
        ...validProject(ALICE_UID, created),
      })
  );

  await assertSucceeds(
    aliceDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("projects")
      .doc("proj_1")
      .delete()
  );
});

test("Leaderboard allows owner writes and filtered public reads; blocks unfiltered reads & cross-user writes", async () => {
  const ts = new Date(Date.now() - 5000);
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("leaderboard").doc(ALICE_UID).set(validLeaderboard(ALICE_UID, ts, true))
  );

  // Filtered query matching allow list succeeds
  await assertSucceeds(
    bobDb.collection("leaderboard").where("publicVisible", "==", true).get()
  );

  // Unfiltered leaderboard query fails
  await assertFails(bobDb.collection("leaderboard").get());

  // Cross-user write fails
  await assertFails(
    bobDb
      .collection("leaderboard")
      .doc(ALICE_UID)
      .update({ xp: 0, updatedAt: new Date(Date.now() - 1000) })
  );
});
