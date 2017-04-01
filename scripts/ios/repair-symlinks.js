
const fs = require('fs');

console.log('Repairing symlinks in WSLibrary.framework');

// Symlinks
try {
  fs.symlinkSync('Versions/A/WSLibrary', 'plugins/com.plugin.widespace/src/ios/WSLibrary.framework/WSLibrary');
  fs.symlinkSync('Versions/A/Headers', 'plugins/com.plugin.widespace/src/ios/WSLibrary.framework/Headers');
  fs.symlinkSync('A', 'plugins/com.plugin.widespace/src/ios/WSLibrary.framework/Versions/Current');
}
catch(err) {
    console.log('No need to repair symlinks on your system');
}
