#!/usr/bin/env python
"""
slerp-framework
-------------
Framework to handle simplify development application with,
flask, consulate and sql alchemy together


"""

from setuptools import setup, find_packages

# Dependencies:
#
# if sys.version_info[0] == 2:
# 	dnspython = 'dnspython'
# elif sys.version_info[0] == 3:
# 	dnspython = 'dnspython3'
# else:
# 	raise ValueError('Unsupported python version')

INSTALL_REQUIRES = [
	'Flask-SQLAlchemy',
	'slerp-py',
	'Flask-Script',
	'Flask-Migrate'
]

TESTS_REQUIRE = [

]

setup(
	name='cash-model',
	version='0.0.1',
	url='https://github.com/kiditz/slerpio',
	license='Apache',
	author='Rifky Aditya Bastara',
	author_email='kiditzbastara@gmail.com',
	description='Model for freelearn project',
	long_description=__doc__,
	packages=find_packages(),
	zip_safe=False,
	include_package_data=True,
	platforms='any',
	install_requires=INSTALL_REQUIRES,
	tests_require=TESTS_REQUIRE,
	extras_require={
		'test': TESTS_REQUIRE,
	},
	test_suite='tests',
	
	classifiers=[
		'Development Status :: 1 - Planning',
		'Environment :: Web Environment',
		'Intended Audience :: Developers',
		'License :: OSI Approved :: Apache Software License',
		'Operating System :: OS Independent',
		'Programming Language :: Python',
		'Programming Language :: Python :: 2',
		'Programming Language :: Python :: 2.7',
		'Programming Language :: Python :: 3',
		'Programming Language :: Python :: 3.4',
		'Programming Language :: Python :: 3.5',
		'Programming Language :: Python :: 3.6',
		'Topic :: Internet :: WWW/HTTP :: Dynamic Content',
		'Topic :: Software Development :: Libraries :: Python Modules'
	]
	# entry_points={
	#     'console_scripts': [
	#         'apigen = slerp.apigen:main',
	#         'testgen = slerp.testgen:main'
	#     ],
	# }
)
