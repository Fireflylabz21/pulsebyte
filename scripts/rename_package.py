import os
import shutil

root_dir = "."
old_pkg = "com.fireflylabz.taptap"
new_pkg = "com.fireflylabz.taptap"

old_dir = os.path.join("app", "src", "main", "java", "com", "fireflylabz", "pulsebyte")
new_dir = os.path.join("app", "src", "main", "java", "com", "fireflylabz", "taptap")

test_old_dir = os.path.join("app", "src", "test", "java", "com", "fireflylabz", "pulsebyte")
test_new_dir = os.path.join("app", "src", "test", "java", "com", "fireflylabz", "taptap")

androidTest_old_dir = os.path.join("app", "src", "androidTest", "java", "com", "fireflylabz", "pulsebyte")
androidTest_new_dir = os.path.join("app", "src", "androidTest", "java", "com", "fireflylabz", "taptap")

# 1. Update text content in all files
extensions = (".kt", ".kts", ".xml", ".sql", ".yml", ".py", ".md")
for dirpath, _, filenames in os.walk(root_dir):
    if ".git" in dirpath or "build" in dirpath or ".gradle" in dirpath or "node_modules" in dirpath:
        continue
    for filename in filenames:
        if filename.endswith(extensions):
            filepath = os.path.join(dirpath, filename)
            try:
                with open(filepath, "r", encoding="utf-8") as f:
                    content = f.read()
                
                new_content = content.replace(old_pkg, new_pkg)
                new_content = new_content.replace("TapTapApplication", "TapTapApplication")
                new_content = new_content.replace("TapTapTheme", "TapTapTheme")
                new_content = new_content.replace("TapDatabase", "TapDatabase")
                
                if new_content != content:
                    with open(filepath, "w", encoding="utf-8") as f:
                        f.write(new_content)
                    print(f"Updated references in: {filepath}")
            except Exception as e:
                print(f"Error processing {filepath}: {e}")

# 2. Rename specific files if they exist
pulse_app_path = os.path.join("app", "src", "main", "java", "com", "fireflylabz", "pulsebyte", "TapTapApplication.kt")
# We will move directories first, then rename files

def move_tree(src, dst):
    if os.path.exists(src):
        if not os.path.exists(dst):
            os.makedirs(dst, exist_ok=True)
        for item in os.listdir(src):
            s = os.path.join(src, item)
            d = os.path.join(dst, item)
            if os.path.isdir(s):
                move_tree(s, d)
            else:
                shutil.move(s, d)
        shutil.rmtree(src)
        print(f"Moved directory tree from {src} to {dst}")

move_tree(old_dir, new_dir)
move_tree(test_old_dir, test_new_dir)
move_tree(androidTest_old_dir, androidTest_new_dir)

# Rename TapTapApplication.kt to TapTapApplication.kt
taptap_app_file = os.path.join("app", "src", "main", "java", "com", "fireflylabz", "taptap", "TapTapApplication.kt")
if os.path.exists(taptap_app_file):
    new_app_file = os.path.join("app", "src", "main", "java", "com", "fireflylabz", "taptap", "TapTapApplication.kt")
    os.rename(taptap_app_file, new_app_file)
    print("Renamed TapTapApplication.kt to TapTapApplication.kt")

print("Package rename complete!")
