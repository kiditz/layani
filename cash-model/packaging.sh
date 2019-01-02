source bin/activate
python3 setup.py sdist
# rm ../cash-api/cash-model-0.0.1.tar.gz
# rm ../pulsa_api/cash-model-0.0.1.tar.gz
cp dist/cash-model-0.0.1.tar.gz ../cash-api
cp dist/cash-model-0.0.1.tar.gz ../pulsa_api
cd ../cash-api
source bin/activate
echo 'Install to cash-api'
pip3 uninstall -y cash-model
pip3 install --no-cache-dir --no-index --find-links cash-model-0.0.1.tar.gz cash-model
deactivate
cd ../pulsa_api
source bin/activate
echo 'Install to pulsa_api'
pip3 uninstall -y cash-model
pip3 install --no-cache-dir --no-index --find-links cash-model-0.0.1.tar.gz cash-model


