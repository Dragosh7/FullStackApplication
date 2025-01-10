const { exec } = require('child_process');
const depcheck = require('depcheck');

depcheck(process.cwd(), {}, (unused) => {
  const unusedDependencies = unused.dependencies;
  if (unusedDependencies.length > 0) {
    console.log('Unused dependencies:', unusedDependencies);
    unusedDependencies.forEach((pkg) => {
      exec(`npm uninstall ${pkg}`, (err, stdout, stderr) => {
        if (err) console.error(`Error removing ${pkg}:`, stderr);
        else console.log(`Removed ${pkg}`);
      });
    });
  } else {
    console.log('No unused dependencies found.');
  }
});
