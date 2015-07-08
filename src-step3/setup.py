from distutils.core import setup, Extension
 
c_ext = Extension("JSolve", ["pybind.c", "JSolve.c"])
 
setup(ext_modules=[c_ext])
